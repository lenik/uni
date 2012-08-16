package net.bodz.lapiota.javatools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.bodz.bas.c.java.io.FileFinder;
import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.jvm.stack.Caller;
import net.bodz.bas.lang.Control;
import net.bodz.bas.loader.Classpath;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ArgsParseBy;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.lapiota.util.TypeExtensions.FileParser2;

/**
 * Find the class file defined the specified class
 */
@ProgramName("jwhich")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FindClassResource
        extends BasicCLI {

    /**
     * max depth of directories recurse into
     *
     * @option -r =[DEPTH] default-value=65536
     */
    protected int recursive = 1;

    /**
     * using custom file filter, default find jar/?ar/zip files
     *
     * @option -Ic =CLASS(FileFilter)
     */
    protected FileFilter filter;

    protected List<URL> classpaths = new ArrayList<URL>();

    /**
     * @option -b =FILE|DIR
     */
    @ParseBy(FileParser2.class)
    protected void bootClasspath(File file)
            throws IOException {
        FileFinder finder = new FileFinder(filter, recursive, file);
        for (File f : finder.listFiles()) {
            if (f.isDirectory())
                return;
            URL url = Files.getURL(f);
            L.debug("add boot-classpath: ", url);
            Classpath.addURL(url);
        }
    }

    /**
     * @option -c =FILE|DIR
     */
    @ArgsParseBy(FileParser2.class)
    protected void classpath(File file)
            throws IOException {
        FileFinder finder = new FileFinder(filter, recursive, file);
        for (File f : finder.listFiles()) {
            if (f.isDirectory())
                return;
            URL url = Files.getURL(f);
            L.debug("queue classpath: ", url);
            classpaths.add(url);
        }
    }

    static String libpath(URL url)
            throws IOException {
        String file = url.toString();
        if ("jar".equals(url.getProtocol())) {
            int pos = file.lastIndexOf("!");
            assert pos > 0;
            file = file.substring(4, pos);
        }
        url = new URL(file);
        try {
            file = new File(url.toURI()).getCanonicalPath();
        } catch (URISyntaxException e) {
            throw new Error(e.getMessage(), e);
        }
        return file;
    }

    static URL findResource(ClassLoader loader, String name) {
        String binpath = name.replace('.', '/') + ".class";
        URL url = loader.getResource(binpath);
        if (url == null)
            url = loader.getResource(name);
        return url;
    }

    /**
     * find all libraries required by the specified class
     *
     * @option -t =MAINCLASS
     */
    protected String testClass;

    /**
     * add one argument to the test arguments
     *
     * @option -a =ARG
     */
    protected String[] testArguments;

    /**
     * suppress the output of test program
     *
     * @option -Q
     */
    protected boolean testQuiet = false;

    protected URL tryFind(String name) {
        URL[] urls = classpaths.toArray(new URL[0]);
        URLClassLoader tryLoader = new URLClassLoader(urls);
        return findResource(tryLoader, name);
    }

    /**
     * do test
     *
     * @option -T
     */
    protected void test()
            throws Exception {
        if (testArguments == null)
            testArguments = new String[0];

        List<URL> tryAdds = new ArrayList<URL>();
        URL tryAdd = null;

        for (int itry = 1; itry <= 1000; itry++) {
            PrintStream _out = System.out;
            PrintStream _err = System.err;
            if (testQuiet) {
                OutputStream sout = new ByteArrayOutputStream();
                OutputStream serr = new ByteArrayOutputStream();
                System.setOut(new PrintStream(sout));
                System.setErr(new PrintStream(serr));
            }

            // URL[] urls = tryAdds.toArray(new URL[0]);
            // ClassLoader craftLoader = new URLClassLoader(urls);
            ClassLoader craftLoader = ClassLoader.getSystemClassLoader();
            // Thread thread = Thread.currentThread();
            // ClassLoader callerLoader = Caller.getCallerClassLoader();
            // thread.setContextClassLoader(craftLoader);
            // Class<?> loadClass = craftLoader.loadClass(className);

            Class<?> clazz = Class.forName(testClass);
            Method mainf = clazz.getMethod("main", String[].class);
            try {
                try {
                    L.info("execute ", mainf.getDeclaringClass(), "::", mainf.getName(), "/", testArguments.length);
                    Control.invoke(mainf, null, (Object) testArguments);
                } finally {
                    if (testQuiet) {
                        System.setOut(_out);
                        System.setErr(_err);
                    }
                }
            } catch (NoClassDefFoundError e) {
                String respath = e.getMessage();
                String failClass = respath.replace('/', '.');
                L.warn("try(", itry, ") ", failClass);
                tryAdd = tryFind(failClass);
                if (tryAdd != null) {
                    String lib = libpath(tryAdd);
                    L.info("add required ", lib);
                    URL liburl = Files.getURL(new File(lib));
                    if (tryAdds.contains(liburl)) {
                        L.error("loop fail");
                        break;
                    }
                    tryAdds.add(liburl);
                    // Classpath.addURL(liburl);
                    Classpath.addURL(craftLoader, liburl);
                    continue;
                } else {
                    L.error("failed to find: ", failClass);
                    throw e;
                }
            }
            break;
        }
        L.mesg("test succeeded, the required libraries: ");
        for (URL url : tryAdds)
            L.mesg(libpath(url));

        testClass = null;
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        if (testClass == null) {
            for (URL url : classpaths) {
                L.debug("add classpath: ", url);
                Classpath.addURL(url);
            }

            ClassLoader loader = Caller.getCallerClassLoader(0);
            Iterable<String> strings;
            if (args.length > 0)
                strings = Arrays.asList(args);
            else {
                L.stdout("Enter class names or resource paths: ");
                strings = Files.readByLine(System.in);
            }
            for (String name : strings) {
                name = name.trim();
                URL url = findResource(loader, name);
                if (url == null)
                    L.mesg("No-Class: ", name);
                else
                    L.mesg("Found: ", libpath(url));
            }
        } else {
            if (args.length > 0) {
                if (testArguments == null)
                    testArguments = args;
                else {
                    String[] concat = new String[testArguments.length + args.length];
                    System.arraycopy(testArguments, 0, concat, 0, testArguments.length);
                    System.arraycopy(args, 0, concat, testArguments.length, args.length);
                    testArguments = concat;
                }
            }
            test();
        }
    }

    private static Pattern JAR_EXTENSIONS;
    static {
        JAR_EXTENSIONS = Pattern.compile("(.ar|zip)", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected void _boot()
            throws Exception {
        if (filter == null)
            filter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String ext = FilePath.getExtension(file).toLowerCase();
                    return JAR_EXTENSIONS.matcher(ext).matches();
                }
            };
    }

    public static void main(String[] args)
            throws Exception {
        new FindClassResource().execute(args);
    }

}
