package net.bodz.lapiota.javatools;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.bodz.bas.c.java.io.FileURL;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.jvm.stack.Caller;
import net.bodz.bas.loader.Classpath;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.sio.BCharOut;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.sio.PrintStreamPrintOut;
import net.bodz.bas.vfs.IFile;

/**
 * Generate class proxy/wrapper
 */
@ProgramName("classwrap")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class GenerateClassWrappers
        extends BasicCLI {

    /**
     * prefix string to the generated class
     *
     * @option =WORD
     * @CheckBy(value = Regex.class, param = "\\w+")
     */
    protected String prefix;

    /**
     * suffix string to the generated class
     *
     * @option =WORD
     * @CheckBy(value = Regex.class, param = "\\w+")
     */
    protected String suffix = "W";

    /**
     * wrap fields, do update before/after method calls
     *
     * @option
     */
    protected boolean withFields = false;

    /**
     * don't wrap classes derived any of these
     *
     * @option -x --skip-class =FQCN
     */
    protected List<Class<?>> skipClasses;

    /**
     * @option hidden
     */
    private List<Class<?>> classes = new ArrayList<Class<?>>();

    /**
     * classes to wrap
     *
     * @option --class -c =FQCN
     */
    public void addClass(String fqcn)
            throws ClassNotFoundException {
        addClass(fqcn, true);
    }

    protected int addClass(String fqcn, boolean error) {
        if (fqcn.indexOf('$') != -1)
            if (error)
                throw new IllegalArgumentException("inner class isn't supported");
            else
                return 0;

        Class<?> clazz;
        try {
            clazz = Class.forName(fqcn, false, Caller.getCallerClassLoader(0));
        } catch (Error e) {
            if (error)
                throw e;
            else
                return 0;
        } catch (ClassNotFoundException e) {
            if (error)
                throw new IllegalArgumentException(e.getMessage(), e);
            else
                return 0;
        }

        int modifiers = clazz.getModifiers();
        if (Modifier.isFinal(modifiers))
            if (error)
                throw new IllegalArgumentException("final class");
            else
                return 0;
        if (!Modifier.isPublic(modifiers))
            if (error)
                throw new IllegalArgumentException("not a public type");
            else
                return 0;

        if (!error && skipClasses != null) {
            for (Class<?> skip : skipClasses)
                if (skip.isAssignableFrom(clazz))
                    return 0;
        }

        classes.add(clazz);
        return 1;
    }

    /**
     * add all public classes from the jar
     *
     * @option -j =JARFILE
     */
    public void addJar(File jarfile)
            throws MalformedURLException, IOException {
        Classpath.addURL(FileURL.getURL(jarfile, null));
        JarFile jar = new JarFile(jarfile);
        Enumeration<JarEntry> entries = jar.entries();
        int count = 0;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            int dot = name.lastIndexOf('.');
            if (dot == -1)
                continue;
            String ext = name.substring(dot + 1);
            if (!"class".equals(ext))
                continue;
            name = name.substring(0, dot);
            String fqcn = name.replace('/', '.');
            count += addClass(fqcn, false);
            count++;
        }
        jar.close();
        logger.info("added ", count, " classes from ", jarfile);
    }

    /**
     * add all public classes from the directory
     *
     * @option -d =DIR
     */
    public void addDirectory(File dir)
            throws MalformedURLException, IOException {
        URL dirURL = FileURL.getURL(dir, null);
        Classpath.addURL(dirURL);
        int count = addDirectory(dir, "");
        logger.info("added ", count, " classes from ", dir);
    }

    protected int addDirectory(File dir, String prefix) {
        int count = 0;
        for (File file : dir.listFiles()) {
            String name = file.getName();
            if (file.isDirectory()) {
                count += addDirectory(file, prefix + name + ".");
                continue;
            }
            int dot = name.lastIndexOf('.');
            if (dot == -1)
                continue;
            String ext = name.substring(dot + 1);
            if (!"class".equals(ext))
                continue;
            name = name.substring(0, dot);
            String fqcn = prefix + name;
            count += addClass(fqcn, false);
        }
        return count;
    }

    private static final String PI_CLASSPATH = "%";

    /**
     * add classes defined in the list file
     *
     * @option -l =LIST-FILE
     */
    public void addList(IFile list)
            throws MalformedURLException, IOException {
        int count = 0;
        for (String line : list.tooling()._for(StreamReading.class).lines()) {
            if (line.startsWith(PI_CLASSPATH)) {
                String path = line.substring(PI_CLASSPATH.length()).trim();
                Classpath.addURL(FileURL.getURL(path, null));
                continue;
            }
            String fqcn = line.trim();
            count += addClass(fqcn, false);
        }
        logger.info("added ", count, " classes from ", list);
    }

    /**
     * @option -w =PACKAGE
     * @CheckBy(value = Regex.class, param = "\\w+(\\.\\w+)*")
     */
    protected String wrapPackage;

    /**
     * dest directory to save the generated java files
     *
     * @option required -o
     * @CheckBy(value = FileAccess.class, param = "d")
     */
    protected File out;

    /**
     * output encoding
     *
     * @option -e
     */
    protected Charset encoding = Charset.defaultCharset();

    public void make(Class<?> clazz)
            throws IOException {
        String pkg = clazz.getPackage().getName();
        String wpkg;
        if (wrapPackage == null || wrapPackage.isEmpty())
            wpkg = pkg;
        else
            wpkg = wrapPackage + "." + pkg;
        String subdir = wpkg.replace('.', '/');
        String wname = clazz.getName();
        if (prefix != null)
            wname = prefix + Strings.ucfirst(wname);
        if (suffix != null)
            wname += suffix;
        File dir = new File(out, subdir);
        File file = new File(dir, wname);
        PrintStream fileout = new PrintStream(file, encoding.name());

        IPrintOut out = new PrintStreamPrintOut(fileout);

        out.println("package " + wpkg + ";");
        out.println();

        List<Class<?>> imports = new ArrayList<Class<?>>();
        if (pkg != wpkg)
            imports.add(clazz);

        // TypeVariable<?>[] ctv = clazz.getTypeParameters();

        BCharOut body = new BCharOut();
        body.print("public class " + wname);
        if (clazz.isInterface())
            body.println(" implements " + clazz.getName() + " {");
        else
            body.println(" extends " + clazz.getName() + " {");

        for (Method method : clazz.getMethods()) {
            method.getParameterTypes();
        }

        body.println("}");
        out.println(body);
        fileout.close();
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (Class<?> clazz : classes) {
            logger.status("type ", clazz);
            make(clazz);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new GenerateClassWrappers().execute(args);
    }

}
