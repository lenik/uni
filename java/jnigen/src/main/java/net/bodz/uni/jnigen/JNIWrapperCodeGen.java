package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.bodz.bas.c.loader.scan.ClassCollector;
import net.bodz.bas.c.loader.scan.ClassFinder;
import net.bodz.bas.c.loader.scan.ClassForrest;
import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.c.system.SystemProperties;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.type.overloaded.TypeInfoOptions;

/**
 * Generate C++ helpers for JNI classes.
 */
@ProgramName("jnigen")
public class JNIWrapperCodeGen
        extends BasicCLI
        implements
            JNIAware {

    static final Logger logger = LoggerFactory.getLogger(JNIWrapperCodeGen.class);

    ClassLoader loader;
//    ClassScanner scanner;
    ClassCollector collector;

    /**
     * Scan classes within this package. (and all subpackages)
     *
     * @option -P --package =NAME
     */
    String scanPackage;

    /**
     * Generate for specific class
     *
     * @option -C --class-name =NAME
     */
    List<String> scanClassNames = new ArrayList<>();

    /**
     * Sort members.
     *
     * @option -o
     */
    boolean sortMembers = true;

    /**
     * Specify the output directory.
     *
     * By default, files are saved in src/main/native/ for maven project, or src/ otherwise.
     *
     * @option -O --outdir =DIR
     */
    File outDir;
    static final String MAVEN_DIR = "src/main/native";

    /**
     * Just print to stdout, instead of creating files.
     *
     * @option -c
     */
    boolean stdout;

    /**
     * Use flatten file names. By default, files are organized by directories according to package
     * names.
     *
     * @option -l
     */
    boolean flatten;

    /**
     * Generate codes for ancestor classes (up to the scan-package).
     *
     * In recursive mode, declared members instead of public members are included.
     *
     * @option -r
     */
    boolean recursive;

    /**
     * Generate JNI native implementation template.
     *
     * @option -n --native
     */
    boolean nativeTemplate;

    /**
     * Default none to avoid conflicts with #define(s).
     *
     * @option -d --include-consts
     */
    boolean includeConstFields;

    /**
     * Include protected members (only declared ones).
     *
     * @option -t --protected
     */
    boolean includeProtectedMembers;

    /**
     * Include package private members. (implies --protected)
     *
     * @option -p --package-private
     */
    boolean includePackagePrivateMembers;

    /**
     * Include private members. (implies --package-private)
     *
     * @option -a --all --private
     */
    boolean includePrivateMembers;

    /**
     * Force to overwrite existing native implemetation files.
     *
     * @option -f --force
     */
    boolean forceOverwrite = false;

    /**
     * C++ Header file extension, default .hxx
     *
     * @option
     */
    String headerExtension = ".hxx";

    /**
     * C++ source file extension, default cxx
     *
     * @option
     */
    String sourceExtension = ".cxx";

    ClassSet classSet;
    SourceFormat format = new SourceFormat();

    public JNIWrapperCodeGen() {
        loader = getClass().getClassLoader();
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (outDir == null) {
            String workDir = SystemProperties.getUserDir();
            MavenPomDir pomDir = MavenPomDir.closest(workDir);
            if (pomDir != null)
                outDir = new File(pomDir.getBaseDir(), MAVEN_DIR);
            else
                outDir = new File("src");
        }

        if (scanPackage == null && scanClassNames.isEmpty()) {
            System.err.println("nothing to generate.");
            return;
        }

        classSet = figureOutClasses();

        if (sortMembers)
            format.memberOrder = SortOrder.SORTED;

        includePackagePrivateMembers |= includePrivateMembers;
        includeProtectedMembers |= includePackagePrivateMembers;

        for (Class<?> clazz : classSet) {
            logger.info("Generate " + clazz);

            SourceFilesForSingleClass files = new SourceFilesForSingleClass();
            String name = clazz.getSimpleName();
            files.wrapperHeaderFile = file(name + headerExtension, clazz);
            files.wrapperSourceFile = file(name + sourceExtension, clazz);

            String typeInfoName = name + "_class";
            files.typeInfoHeaderFile = file(typeInfoName + headerExtension, clazz);
            files.typeInfoSourceFile = file(typeInfoName + sourceExtension, clazz);

            String jniBaseName = name + "-jni";
            files.jniHeaderFile = file(jniBaseName + headerExtension, clazz);
            files.jniSourceFile = file(jniBaseName + sourceExtension, clazz);

            TypeInfoOptions options = format.toTypeInfoOptions();
            options.declaredOnly = classSet.contains(clazz.getSuperclass());
            options._protected = includeProtectedMembers;
            options.packagePrivate = includePackagePrivateMembers;
            options._private = includePrivateMembers;
            options.includeConsts = includeConstFields;

            ClassMembers members = new ClassMembers();
            members.addMembers(clazz, options);

            run(files, members, new JNIWrapper_h(clazz));
            run(files, members, new JNIWrapper_cxx(clazz));
            run(files, members, new JNITypeInfo_h(clazz));
            run(files, members, new JNITypeInfo_cxx(clazz));

            if (nativeTemplate)
                if (! members.getNativeMethodNames().isEmpty()) {
                    if (forceOverwrite || ! files.jniHeaderFile.exists())
                        run(files, members, new JNINative_h(clazz));
                    if (forceOverwrite || ! files.jniSourceFile.exists())
                        run(files, members, new JNINative_cxx(clazz));
                }
        }
    }

    ClassSet figureOutClasses()
            throws IOException, ClassNotFoundException {
        ClassSet classes = new ClassSet();
        ClassSet term = new ClassSet();
        term.add(Object.class);
        term.add(String.class);

        LinkedList<Class<?>> queue = new LinkedList<>();

        if (scanPackage != null) {
            ClassFinder scanner = new ClassFinder();
            ClassForrest forrest = scanner.addPackage(loader, scanPackage);
            queue.addAll(forrest.getParseList());
        }

        if (scanClassNames != null)
            for (String name : scanClassNames) {
                Class<?> clazz = Class.forName(name, false, loader);
                queue.add(clazz);
            }

        while (true) {
            Class<?> clazz = queue.poll();
            if (clazz == null)
                break;
            classes.add(clazz);

            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null)
                if (recursive) {
                    String superPackageName = superclass.getPackage().getName();
                    if (TypeNames.packageContains(scanPackage, superPackageName)) {
                        if (! term.add(superclass))
                            queue.addFirst(superclass);
                    }
                }
        }
        return classes;
    }

    void run(SourceFilesForSingleClass sourceFiles, ClassMembers members, JNISourceBuilder builder)
            throws IOException {
        builder.setSourceFiles(sourceFiles);
        builder.classMap = classSet;
        builder.setMembers(members);
        builder.setFormat(format);

        File file = builder.getPreferredFile();
        ITreeOut out = null;
        if (stdout) {
            out = Stdio.cout.indented();
            out.println("/** FILE: " + file + " */");
        }

        long classTime = builder.getLastModifiedTime();
        long fileTime = file.lastModified();
        boolean expired = fileTime == 0 //
                || classTime == 0 //
                || fileTime < classTime;

        if (expired || forceOverwrite) {
            if (stdout) {
                builder.buildSource(out, file);
            } else {
                builder.buildSource(file);
            }
        } else {
            if (stdout)
                out.println("    // updated.");
        }
    }

    File file(String name, Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        String path;
        if (flatten) {
            String packagePrefix = packageName.replace('.', '_');
            path = packagePrefix + "_" + name;
        } else {
            String packageDir = packageName.replace('.', '/');
            path = packageDir + "/" + name;
        }
        return new File(outDir, path);
    }

    public static void main(String[] args)
            throws Exception {
        new JNIWrapperCodeGen().execute(args);
    }

}
