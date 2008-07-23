package net.bodz.lapiota.ant.tasks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli._RunInfo;
import net.bodz.bas.cli.util.Rcs;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.VersionInfo;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.Caller;
import net.bodz.bas.lang.err.IdentifiedException;
import net.bodz.bas.text.interp.VariableExpand;
import net.bodz.bas.types.util.Annotations;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.annotations.LoadBy;
import net.bodz.lapiota.annotations.ProgramName;
import net.bodz.lapiota.programs.ClassLauncher;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class GenerateBatches extends Task {

    private int                 loglevel = 1;
    private String              srcdir;
    private String              batdir;
    private String              prefix   = "";
    private String              charset  = "utf-8";
    private Map<String, String> varmap;
    private VariableExpand      templateParser;
    private Set<String>         generated;

    public GenerateBatches() {
        varmap = new HashMap<String, String>();
        varmap.put("GENERATOR", "GenerateBatches 0." + verinfo.getVersion()
                + "  Last updated: " + verinfo.getDate());
        templateParser = new VariableExpand(varmap);
        generated = new HashSet<String>();
    }

    public int getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(int loglevel) {
        this.loglevel = loglevel;
    }

    public String getSrcdir() {
        return srcdir;
    }

    public void setSrcdir(String srcdir) {
        this.srcdir = srcdir;
    }

    public String getBatdir() {
        return batdir;
    }

    public void setBatdir(String batdir) {
        this.batdir = batdir;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            prefix = "";
        else
            prefix = prefix.trim();
        if (prefix.length() > 0)
            if (prefix.endsWith("."))
                prefix += ".";
        this.prefix = prefix;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    protected void log1(String msg) {
        if (loglevel >= 1)
            System.out.println(msg);
    }

    protected void log2(String msg) {
        if (loglevel >= 2)
            System.out.println(msg);
    }

    protected void generate(Class<?> clazz) throws IOException {
        String name = clazz.getName();
        if (generated.contains(name))
            return;

        File batdirf = new File(batdir);
        if (!batdirf.exists())
            if (!batdirf.mkdirs())
                throw new IOException("failed to mkdir " + batdir);
        String batName = Annotations.getAnnotation(clazz, ProgramName.class);
        if (batName == null)
            batName = clazz.getSimpleName();
        File batf = new File(batdirf, batName + ".bat");

        varmap.put("NAME", name);

        String launch = "";
        Class<? extends ClassLoader> loaderClass = Annotations.getAnnotation(
                clazz, LoadBy.class, true);
        if (loaderClass != null) {
            launch = ClassLauncher.class.getName() + " " + loaderClass.getName();
        }
        varmap.put("LAUNCH", launch);

        StringBuffer morecp1 = new StringBuffer();
        StringBuffer morecpR = new StringBuffer();
        StringBuffer morecpF = new StringBuffer();

        for (Class<?> c : Types.getTypeChain(clazz)) {
            RunInfo runInfo = c.getAnnotation(RunInfo.class);
            if (runInfo == null)
                continue;
            for (String lib : runInfo.lib()) {
                String r = "%lib" + lib + "%";
                String f = _RunInfo.libversions.getProperty("lib" + lib, lib
                        + ".jar");
                morecpR.append("set _morecp=%_morecp%;%JAVA_LIB%\\" + r
                        + "\n    ");
                morecpF.append("set _morecp=%_morecp%;%JAVA_LIB%\\" + f
                        + "\n    ");
            }
            for (String jar : runInfo.jar()) {
                String f = jar + ".jar";
                morecp1.append("set _morecp=%_morecp%;%JAVA_LIB%\\" + f
                        + "\n    ");
            }
        }
        varmap.put("MORECP_1", morecp1.toString());
        varmap.put("MORECP_R", morecpR.toString());
        varmap.put("MORECP_F", morecpF.toString());

        String inst = templateParser.process(template);

        if (Files.copyDiff(inst.getBytes(), batf))
            log1("write " + batf);

        generated.add(name);
    }

    protected void findmains(File dirf, final String pkg) throws IOException {
        if (!dirf.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + dirf);

        String[] files = dirf.list();

        for (String name : files) {
            int dot = name.lastIndexOf('.');
            if (dot <= 0)
                continue;
            String ext = name.substring(dot + 1);
            if (!"java".equals(ext) && !"class".equals(ext))
                continue;
            name = name.substring(0, dot);
            if (name.contains("$")) // ignore inner classes
                continue;
            String fqcn = pkg + name;
            ClassLoader loader = Caller.getCallerClassLoader();

            // skip if Boot class exists.
            try {
                Class.forName(fqcn + "Boot", false, loader);
                continue;
            } catch (Throwable t) {
            }

            Class<?> clazz = null;
            try {
                log2("try " + fqcn);
                clazz = Class.forName(fqcn, false, loader);
            } catch (Throwable t) {
                System.err.println("failed to load class " + name + ": " + t);
                continue;
            }
            try {
                clazz.getMethod("main", String[].class);
                log2("    main-class: " + clazz);
            } catch (NoSuchMethodException e) {
                continue;
            } catch (Throwable t) {
                System.err.println("failed to get main of " + name + ": " + t);
                continue;
            }
            generate(clazz);
        }

        for (String subdir : files) {
            File subf = new File(dirf, subdir);
            if (!subf.isDirectory())
                continue;
            log2("dir " + subf);
            findmains(subf, pkg + subdir + ".");
        }
    }

    @Override
    public void execute() throws BuildException {
        try {
            findmains(new File(srcdir), prefix);
        } catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public synchronized void reset() {
        generated.clear();
    }

    private static VersionInfo verinfo;
    private static String      template;

    static {
        verinfo = Rcs.parseId(GenerateBatches.class);
        try {
            template = Files.readAll(Files.classData(GenerateBatches.class,
                    "bat.tmpl"), "utf-8");
        } catch (IOException e) {
            throw new IdentifiedException(e.getMessage(), e);
        }
    }

}
