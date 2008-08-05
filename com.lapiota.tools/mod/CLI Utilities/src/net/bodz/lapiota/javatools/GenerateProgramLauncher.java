package net.bodz.lapiota.javatools;

import static net.bodz.bas.types.util.Strings.qq;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.annotations.ClassInfo;
import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIConfig;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.Files;
import net.bodz.bas.io.CharOuts.Buffer;
import net.bodz.bas.lang.Caller;
import net.bodz.bas.lang.err.IdentifiedException;
import net.bodz.bas.loader.JavaLibraryLoader;
import net.bodz.bas.text.interp.Interps;
import net.bodz.bas.types.util.Annotations;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.annotations.LoadBy;
import net.bodz.lapiota.annotations.ProgramName;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("Generate program launcher for java applications")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("mkbat")
public class GenerateProgramLauncher extends BatchProcessCLI {

    // private String prefix = "";
    private Map<String, String> varmap;
    private Set<String>         generated;

    public GenerateProgramLauncher() {
        generated = new HashSet<String>();
        varmap = new HashMap<String, String>();
        varmap.put("PROPERTY_LIB_LOADED", CLIConfig.PROPERTY_LIB_LOADED);
        ClassInfo classInfo = _loadClassInfo();
        varmap.put("GENERATOR", GenerateProgramLauncher.class.getSimpleName()
                + " " + classInfo.getVersionString() + ", "
                + classInfo.getDateString());
    }

    @Override
    protected void _boot() throws Throwable {
        // Classpath.dumpURLs(Caller.getCallerClassLoader(), CharOuts.stderr);
    }

    @Override
    protected ProcessResult doFile(File file) throws Throwable {
        String ext = Files.getExtension(file, true);
        if (!".java".equals(ext) && !".class".equals(ext))
            return ProcessResult.pass();
        String name = Files.getName(file);
        if (name.contains("$")) // ignore inner classes
            return ProcessResult.pass("inner");

        File bootFile = new File(file.getParentFile(), name + "Boot." + ext);
        if (bootFile.exists())
            return ProcessResult.pass("boot");

        String className = getRelativeName(file);
        className = className.substring(0, className.length() - ext.length());
        className = className.replace('\\', '/');
        className = className.replace('/', '.');
        if (generated.contains(className))
            return ProcessResult.pass("repeat");

        ClassLoader loader = Caller.getCallerClassLoader();

        Class<?> clazz = null;
        try {
            L.d.P("try ", className);
            clazz = Class.forName(className, false, loader);
        } catch (Throwable t) {
            return ProcessResult.err(t, "loadc");
        }
        try {
            clazz.getMethod("main", String[].class);
            L.i.P("    main-class: ", clazz);
        } catch (NoSuchMethodException e) {
            return ProcessResult.pass("notapp");
        } catch (Throwable t) {
            return ProcessResult.err(t, "loadf");
        }
        generate(clazz);
        return ProcessResult.pass("ok");
    }

    protected void generate(Class<?> clazz) throws IOException {
        String name = clazz.getName();

        String batName = Annotations.getAnnotation(clazz, ProgramName.class);
        if (batName == null) {
            batName = clazz.getSimpleName();
        }
        File batf = getOutputFile(batName + ".bat");
        batf.getParentFile().mkdirs();

        // varmap.clear();
        varmap.put("TEMPLATE", new File(batTemplate.getPath()).getName());
        varmap.put("NAME", name);

        String launch = "";
        Class<? extends ClassLoader> loaderClass = Annotations.getAnnotation(
                clazz, LoadBy.class, true);
        if (loaderClass != null) {
            launch = ClassLauncher.class.getName() + " "
                    + loaderClass.getName();
        }
        varmap.put("LAUNCH", launch);

        Buffer loadlibs = new Buffer();
        JavaLibraryLoader libloader = JavaLibraryLoader.DEFAULT;
        for (Class<?> c : Types.getTypeChain(clazz)) {
            RunInfo runInfo = c.getAnnotation(RunInfo.class);
            if (runInfo == null)
                continue;
            for (String lib : runInfo.lib()) {
                File f = libloader.findLibraryFile(lib);
                String fname;
                if (f == null) {
                    fname = lib + ".jar";
                    L.w.P("lib ", lib, " => ", fname);
                } else
                    fname = f.getName();
                String loadlib = "call :load " + qq(lib) + " " + qq(fname);
                loadlibs.println("    " + loadlib);
            }
        }
        varmap.put("LOADLIBS_0", "");
        varmap.put("LOADLIBS", loadlibs.toString());

        String inst = Interps.dereference(batTemplateBody, varmap);

        if (force) {
            Files.write(batf, inst.getBytes(), batf);
            L.m.P("write ", batf);
        } else if (Files.copyDiff(inst.getBytes(), batf))
            L.m.P("save ", batf);
    }

    static URL    batTemplate;
    static String batTemplateBody;

    static {
        try {
            batTemplate = Files.classData(GenerateProgramLauncher.class,
                    "batTemplate");
            batTemplateBody = Files.readAll(batTemplate, "utf-8");
        } catch (IOException e) {
            throw new IdentifiedException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Throwable {
        new GenerateProgramLauncher().run(args);
    }

}
