package net.bodz.lapiota.ant.tasks;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.bodz.lapiota.util.Rcs;
import net.bodz.lapiota.util.RcsKeywords;
import net.bodz.lapiota.util.Template;
import net.sf.freejava.err.IdentifiedException;
import net.sf.freejava.util.Classes;
import net.sf.freejava.util.Files;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

@RcsKeywords(id = "$Id$")
public class GenerateBatches extends Task {

    private int                 loglevel = 1;
    private String              srcdir;
    private String              batdir;
    private String              prefix   = "";
    private String              charset  = "utf-8";
    private Map<String, String> varmap;
    private Template            tmpl;
    private Set<String>         generated;

    public GenerateBatches() {
        varmap = new HashMap<String, String>();
        varmap.put("GENERATOR", "GenerateBatches 0." + id.get("rev")
                + "  Last updated: " + id.get("date"));
        tmpl = new Template(template, varmap);
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

    protected void generate(String fqcn, String base) throws IOException {
        if (generated.contains(fqcn))
            return;

        File batdirf = new File(batdir);
        if (!batdirf.exists())
            if (!batdirf.mkdirs())
                throw new IOException("failed to mkdir " + batdir);
        File batf = new File(batdirf, base + ".bat");
        varmap.put("NAME", fqcn);
        String inst = tmpl.generate();

        log1("write " + batf);
        PrintStream out = Files.writeTo(batf, charset);
        out.println(inst);
        out.close();

        generated.add(fqcn);
    }

    protected void findmains(File dirf, final String pkg) throws IOException {
        if (!dirf.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + dirf);

        String[] files = dirf.list();

        for (String name : files) {
            int dot = name.indexOf('.');
            if (dot <= 0)
                continue;
            String ext = name.substring(dot + 1);
            if (!"java".equals(ext) && !"class".equals(ext))
                continue;
            name = name.substring(0, dot);
            String fqcn = pkg + name;
            ClassLoader loader = Classes.getCallerClassLoader();
            Class<?> clazz = null;
            try {
                log2("try " + fqcn);
                clazz = Class.forName(fqcn, false, loader);
            } catch (ClassNotFoundException e) {
                continue;
            }
            try {
                clazz.getMethod("main", String[].class);
                log2("    main-class: " + clazz);
            } catch (SecurityException e) {
                throw new IdentifiedException(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                continue;
            }
            generate(fqcn, name);
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

    private static Map<String, Object> id;
    private static String              template;

    static {
        id = Rcs.parseId(GenerateBatches.class);
        try {
            template = Files.readAll(GenerateBatches.class, //
                    "bat.tmpl", "utf-8");
        } catch (IOException e) {
            throw new IdentifiedException(e.getMessage(), e);
        }
    }

}
