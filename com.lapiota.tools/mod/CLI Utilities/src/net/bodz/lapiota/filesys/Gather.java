package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.types.PrefixMap;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.wrappers.BasicCLI;

@BootInfo(syslibs = { "dom4j", "jaxen" })
@Doc("Gather/Centralize Files")
@RcsKeywords(id = "$Id$")
@Version( { 0, 0 })
public class Gather extends BasicCLI {

    @Option(alias = "u", doc = "updata only")
    boolean updateOnly;

    @Option(alias = "d", doc = "deliver to srcdirs")
    boolean deliver;

    @Option(alias = "f", doc = "force to overwrite")
    boolean force;

    Method  copyMain;

    @Option(alias = "c", vnam = "MAIN-CLASS", doc = "using specified copy program")
    void copyBy(Class<?> copyType) {
        try {
            copyMain = copyType.getMethod("main", String[].class); //$NON-NLS-1$
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    void copy(File src, File dst) throws IOException {
        if (updateOnly && dst.exists()) {
            long srcv = src.lastModified();
            long dstv = dst.lastModified();
            if (dstv >= srcv) {
                L.detail(CLINLS.getString("Gather.skip"), dst); //$NON-NLS-1$
                return;
            }
        }
        dst.getParentFile().mkdirs();
        Files.copy(src, dst);
    }

    @Option
    String gatherDir = ".gather"; //$NON-NLS-1$

    @Override
    protected void _boot() throws Exception {
        if (copyMain == null)
            copyBy(GatherCopy.class);
    }

    class GMap {
        private File              dstdir;
        private PrefixMap<String> prefixes;
        private Map<File, File>   src2dst;

        public GMap(File dstdir) {
            assert dstdir != null : "null dstdir"; //$NON-NLS-1$
            this.dstdir = Files.canoniOf(dstdir);
            this.src2dst = new HashMap<File, File>();
        }

        void setPrefix(String prefix, String s) {
            if (prefixes == null)
                prefixes = new PrefixMap<String>();
            s = expand(s);
            prefixes.put(prefix, s);
        }

        String expand(String s) {
            if (prefixes == null)
                return s;
            String prefix = prefixes.floorKey(s);
            if (prefix == null)
                return s;
            String expanded = prefixes.get(prefix);
            return expanded + s.substring(prefix.length());
        }

        void add(String srcdir, String srcfile, String dstfile) {
            srcdir = expand(srcdir);
            String srcwild = srcdir + "/" + srcfile; //$NON-NLS-1$
            List<File> srcs = Files.find(srcwild);
            if (srcs == null)
                throw new IllegalArgumentException(
                        CLINLS.getString("Gather.srcIsntExisted") + srcwild); //$NON-NLS-1$
            if (srcs.size() > 1)
                throw new IllegalArgumentException(CLINLS.getString("Gather.tooManyMatchedSrc") //$NON-NLS-1$
                        + Strings.join("\n", srcs)); //$NON-NLS-1$
            File src = Files.canoniOf(srcs.get(0));
            File dst = Files.canoniOf(dstdir, dstfile);
            src2dst.put(src, dst);
        }

        void gather() throws IOException {
            for (Entry<File, File> e : src2dst.entrySet()) {
                File src = e.getKey();
                File dst = e.getValue();
                L.mesg(CLINLS.getString("Gather.get"), src); //$NON-NLS-1$
                copy(src, dst);
            }
        }

        void deliver() throws IOException {
            for (Entry<File, File> e : src2dst.entrySet()) {
                File src = e.getKey();
                File dst = e.getValue();
                L.mesg(CLINLS.getString("Gather.put"), src); //$NON-NLS-1$
                copy(dst, src);
            }
        }
    }

    @Override
    protected void doFileArgument(File dstdir) throws Exception {
        if (!dstdir.isDirectory())
            throw new IllegalArgumentException(CLINLS.getString("Gather.notDirectory") + dstdir); //$NON-NLS-1$

        File gatherd = new File(dstdir, gatherDir);
        if (!gatherd.isDirectory())
            throw new IllegalArgumentException(
                    CLINLS.getString("Gather.notGatheredTarget") + dstdir); //$NON-NLS-1$

        GMap gmap = new GMap(dstdir);
        File prefixf = new File(gatherd, ".prefix"); //$NON-NLS-1$
        if (prefixf.exists()) {
            for (String line : Files.readByLine(prefixf)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) //$NON-NLS-1$
                    continue;
                int eq = line.indexOf('=');
                if (eq == -1)
                    throw new ParseException(CLINLS.getString("Gather.invalidPrefix") + line); //$NON-NLS-1$
                String prefix = line.substring(0, eq).trim();
                String expanded = line.substring(eq + 1).trim();
                gmap.setPrefix(prefix, expanded);
            }
        }

        for (File gfile : gatherd.listFiles()) {
            if (gfile.isDirectory())
                continue;
            String base = gfile.getName();
            if (base.startsWith(".")) //$NON-NLS-1$
                continue;
            String srcdir = null;
            for (String line : Files.readByLine(gfile)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) //$NON-NLS-1$
                    continue;
                if (line.endsWith(":")) { //$NON-NLS-1$
                    srcdir = line.substring(0, line.length() - 1);
                    continue;
                }
                if (srcdir == null)
                    throw new IllegalStateException(
                            CLINLS.getString("Gather.srcdirIsntSet") + gfile + ": \n" + line); //$NON-NLS-1$ //$NON-NLS-2$
                String srcfile = line;
                String dst = srcfile;
                int eq = line.indexOf('=');
                if (eq != -1) {
                    dst = line.substring(0, eq).trim();
                    srcfile = line.substring(eq + 1).trim();
                }
                gmap.add(srcdir, srcfile, dst);
            }
        }
        if (deliver)
            gmap.deliver();
        else
            gmap.gather();
    }

    public static void main(String[] args) throws Exception {
        new Gather().run(args);
    }

}
