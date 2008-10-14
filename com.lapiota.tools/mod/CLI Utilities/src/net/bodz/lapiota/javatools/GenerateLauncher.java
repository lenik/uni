package net.bodz.lapiota.javatools;

import static net.bodz.bas.types.util.ArrayOps.Bytes;
import static net.bodz.bas.types.util.Strings.qq;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.bodz.bas.a.ClassInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIConfig;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli._RunInfo;
import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.cli.util.Launcher;
import net.bodz.bas.io.Files;
import net.bodz.bas.io.CharOuts.Buffer;
import net.bodz.bas.lang.Caller;
import net.bodz.bas.lang.err.IdentifiedException;
import net.bodz.bas.loader.JavaLibraryLoader;
import net.bodz.bas.text.interp.Interps;
import net.bodz.bas.types.TextMap;
import net.bodz.bas.types.TextMap.HashTextMap;
import net.bodz.bas.types.util.Annotations;
import net.bodz.bas.types.util.Ns;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.a.LoadBy;
import net.bodz.lapiota.a.ProgramName;
import net.bodz.lapiota.win32.Fix_BatBB;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("Generate program launcher for java applications")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("mkbat")
public class GenerateLauncher extends BatchProcessCLI {

    // private String prefix = "";
    private TextMap<String> varmap;
    private Set<String>     generated;

    public GenerateLauncher() {
        generated = new HashSet<String>();
        varmap = new HashTextMap<String>();
        varmap.put("PROPERTY_LIB_LOADED", CLIConfig.PROPERTY_LIB_LOADED);
        ClassInfo classInfo = _loadClassInfo();
        varmap.put("GENERATOR", GenerateLauncher.class.getSimpleName() + " "
                + classInfo.getVersionString() + ", "
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

        int modifiers = clazz.getModifiers();
        if (!Modifier.isPublic(modifiers))
            return ProcessResult.pass("local");

        LoadBy loadBy = Ns.getN(clazz, LoadBy.class);
        if (loadBy != null) {
            int preload = loadBy.preload();
            if (preload != 0) {
                _RunInfo runInfo = _RunInfo.parse(clazz);
                if ((LoadBy.BOOT & preload) != 0)
                    runInfo.loadBoot();
                if ((LoadBy.LIB & preload) != 0)
                    runInfo.loadLibraries();
                if ((LoadBy.DELAYED & preload) != 0)
                    runInfo.loadDelayed();
            }
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

        String batName = (String) Annotations
                .getValue(clazz, ProgramName.class);
        if (batName == null) {
            batName = clazz.getSimpleName();
            batName = batName.toLowerCase(); // option?
        }
        File batf = getOutputFile(batName + ".bat");
        batf.getParentFile().mkdirs();

        // varmap.clear();
        varmap.put("TEMPLATE", new File(batTemplate.getPath()).getName());
        varmap.put("NAME", name);

        String launch = "";

        LoadBy loadBy = Ns.getN(clazz, LoadBy.class);
        if (loadBy != null) {
            Class<? extends ClassLoader> loaderClass = loadBy.value();
            if (loaderClass == ClassLoader.class)
                loaderClass = null;
            Class<? extends Launcher> launcherClass = loadBy.launcher();
            if (launcherClass == Launcher.class)
                launcherClass = null;
            if (launcherClass == null) {
                if (loaderClass != null)
                    launch = ClassLauncher.class.getName() + " "
                            + loaderClass.getName();
            } else {
                launch = launcherClass.getName();
            }
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
        byte[] batData = inst.getBytes();
        byte[] batFixed = fix_BatBB.doFileEdit(batData);
        if (!Bytes.equals(batData, batFixed))
            L.m.P("bat label boundary fixed: ", batf);
        if (force) {
            Files.write(batf, batFixed, batf);
            L.m.P("write ", batf);
        } else if (Files.copyDiff(batFixed, batf))
            L.m.P("save ", batf);
    }

    static URL       batTemplate;
    static String    batTemplateBody;
    static Fix_BatBB fix_BatBB;

    static {
        try {
            batTemplate = Files
                    .classData(GenerateLauncher.class, "batTemplate");
            batTemplateBody = Files.readAll(batTemplate, "utf-8");
        } catch (IOException e) {
            throw new IdentifiedException(e.getMessage(), e);
        }
        fix_BatBB = new Fix_BatBB();
    }

    public static void main(String[] args) throws Throwable {
        new GenerateLauncher().run(args);
    }

}
