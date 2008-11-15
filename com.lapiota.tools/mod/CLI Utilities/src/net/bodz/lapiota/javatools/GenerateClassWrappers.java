package net.bodz.lapiota.javatools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.CheckBy;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.io.Files;
import net.bodz.bas.io.CharOuts.Buffer;
import net.bodz.bas.lang.Caller;
import net.bodz.bas.lang.util.Classpath;
import net.bodz.bas.types.Checks.FileAccess;
import net.bodz.bas.types.Checks.Regex;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("Generate class proxy/wrapper")
@ProgramName("classwrap")
@RcsKeywords(id = "$Id$")
@Version( { 0, 0 })
public class GenerateClassWrappers extends BasicCLI {

    @Option(vnam = "WORD", doc = "prefix string to the generated class")
    @CheckBy(value = Regex.class, param = "\\w+")
    protected String         prefix;

    @Option(vnam = "WORD", doc = "suffix string to the generated class")
    @CheckBy(value = Regex.class, param = "\\w+")
    protected String         suffix     = "W";

    @Option(doc = "wrap fields, do update before/after method calls")
    protected boolean        withFields = false;

    @Option(name = "skip-class", alias = "x", vnam = "FQCN", valtype = Class.class, doc = "don't wrap classes derived any of these")
    protected List<Class<?>> skipClasses;

    @Option(hidden = true, valtype = Class.class)
    private List<Class<?>>   classes    = new ArrayList<Class<?>>();

    @Option(name = "class", alias = "c", vnam = "FQCN", doc = "classes to wrap")
    public void addClass(String fqcn) throws ClassNotFoundException {
        addClass(fqcn, true);
    }

    protected int addClass(String fqcn, boolean error) {
        if (fqcn.indexOf('$') != -1)
            if (error)
                throw new IllegalArgumentException(
                        "inner class isn't supported");
            else
                return 0;

        Class<?> clazz;
        try {
            clazz = Class.forName(fqcn, false, Caller.getCallerClassLoader());
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

    @Option(alias = "j", vnam = "JARFILE", doc = "add all public classes from the jar")
    public void addJar(File jarfile) throws MalformedURLException, IOException {
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

    @Option(alias = "d", vnam = "DIR", doc = "add all public classes from the directory")
    public void addDirectory(File dir) throws MalformedURLException,
            IOException {
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

    @Option(alias = "l", vnam = "LIST-FILE", doc = "add classes defined in the list file")
    public void addList(File list) throws MalformedURLException, IOException {
        int count = 0;
        for (String line : Files.readByLine(list)) {
            if (line.startsWith(PI_CLASSPATH)) {
                String path = line.substring(PI_CLASSPATH.length()).trim();
                Classpath.addURL(Files.canoniOf(path).toURI().toURL());
                continue;
            }
            String fqcn = line.trim();
            count += addClass(fqcn, false);
        }
        L.i.P("added ", count, " classes from ", list);
    }

    @Option(alias = "w", vnam = "PACKAGE")
    @CheckBy(value = Regex.class, param = "\\w+(\\.\\w+)*")
    protected String  wrapPackage;

    @Option(alias = "o", required = true, doc = "dest directory to save the generated java files")
    @CheckBy(value = FileAccess.class, param = "d")
    protected File    out;

    @Option(alias = "e", doc = "output encoding")
    protected Charset encoding = Charset.defaultCharset();

    public void make(Class<?> clazz) throws IOException {
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
        OutputStream fileout = new FileOutputStream(file);
        CharOut out = CharOuts.get(fileout, encoding.name());

        out.println("package " + wpkg + ";");
        out.println();

        List<Class<?>> imports = new ArrayList<Class<?>>();
        if (pkg != wpkg)
            imports.add(clazz);

        // TypeVariable<?>[] ctv = clazz.getTypeParameters();

        Buffer body = new CharOuts.Buffer();
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
    protected void doMain(String[] args) throws Throwable {
        for (Class<?> clazz : classes) {
            L.i.sig("type ", clazz);
            make(clazz);
        }
    }

    public static void main(String[] args) throws Throwable {
        new GenerateClassWrappers().run(args);
    }

}
