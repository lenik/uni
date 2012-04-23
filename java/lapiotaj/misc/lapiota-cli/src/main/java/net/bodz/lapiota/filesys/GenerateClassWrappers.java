package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.jvm.stack.Caller;
import net.bodz.bas.loader.Classpath;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.meta.util.ValueType;
import net.bodz.bas.sio.BCharOut;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.sio.PrintStreamPrintOut;

/**
 * Generate class proxy/wrapper
 */
@Version({ 0, 0 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("classwrap")
public class GenerateClassWrappers
        extends BasicCLI {

    /**
     * Prefix string to the generated class
     *
     * @option =WORD
     */
// @Option(check = Regex.class, checkinfo = "\\w+")
    protected String prefix;

    /**
     * Suffix string to the generated class
     *
     * @option =WORD
     */
// @Option(check = Regex.class, checkinfo = "\\w+")
    protected String suffix = "W";

    /**
     * Wrap fields, do update before/after method calls
     *
     * @option
     */
    protected boolean withFields = false;

    /**
     * Don't wrap classes derived any of these
     *
     * @option -x --skip-class =FQCN
     */
    @ValueType(Class.class)
    protected List<Class<?>> skipClasses;

    /**
     * @option hidden
     */
    @ValueType(Class.class)
    private List<Class<?>> classes = new ArrayList<Class<?>>();

    /**
     * Classes to wrap
     *
     * @option -c --class =FQCN
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
            clazz = Class.forName(fqcn, false, Caller.getCallerClassLoader(1));
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
     * Add all public classes from the jar
     *
     * @option -j =JARFILE
     */
    public void addJar(File jarfile)
            throws MalformedURLException, IOException {
        Classpath.addURL(jarfile.toURI().toURL());
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
        L.i.P("added ", count, " classes from ", jarfile);
    }

    /**
     * Add all public classes from the directory
     *
     * @option -d =DIR
     */
    public void addDirectory(File dir)
            throws MalformedURLException, IOException {
        Classpath.addURL(dir.toURI().toURL());
        int count = addDirectory(dir, "");
        L.i.P("added ", count, " classes from ", dir);
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
     * Add classes defined in the list file
     *
     * @option -l =LIST-FILE
     */
    public void addList(File list)
            throws MalformedURLException, IOException {
        int count = 0;
        for (String l : Files.readByLine(list)) {
            if (l.startsWith(PI_CLASSPATH)) {
                String path = l.substring(PI_CLASSPATH.length()).trim();
                Classpath.addURL(new File(path).toURI().toURL());
                continue;
            }
            String fqcn = l.trim();
            count += addClass(fqcn, false);
        }
        L.i.P("added ", count, " classes from ", list);
    }

    /**
     * @option -w =PACKAGE
     */
// @Option(check = Regex.class, checkinfo = "\\w+(\\.\\w+)*")
    protected String wrapPackage;

    /**
     * Dest directory to save the generated java files
     *
     * @option -o required
     */
    // @Option(check = FileAccess.class, checkinfo = "d")
    protected File out;

    /**
     * Output encoding
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
        IPrintOut out = new PrintStreamPrintOut(new PrintStream(file, encoding.name()));

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
        out.close();
    }

    @Override
    protected void _main(String[] args)
            throws Throwable {
        for (Class<?> clazz : classes) {
            L.i.sig("type ", clazz);
            make(clazz);
        }
    }

    public static void main(String[] args)
            throws Throwable {
        new GenerateClassWrappers().climain(args);
    }

}
