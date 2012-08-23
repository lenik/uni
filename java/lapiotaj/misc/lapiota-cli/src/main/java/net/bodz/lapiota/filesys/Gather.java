package net.bodz.lapiota.filesys;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.io.FileWild;
import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.collection.preorder.PrefixMap;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.vfs.IFile;

/**
 * Gather/Centralize Files
 */
@BootInfo(syslibs = { "dom4j", "jaxen" })
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class Gather
        extends BasicCLI {

    /**
     * Updata only
     *
     * @option -u
     */
    boolean updateOnly;

    /**
     * Deliver to srcdirs
     *
     * @option -d
     */
    boolean deliver;

    /**
     * Force to overwrite
     *
     * @option -f
     */
    boolean force;

    Method copyMain;

    /**
     * using specified copy program
     *
     * @option -c =MAIN-FQCN
     */
    void copyBy(Class<?> copyType) {
        try {
            copyMain = copyType.getMethod("main", String[].class);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    void copy(File src, File dst)
            throws IOException {
        if (updateOnly && dst.exists()) {
            long srcv = src.lastModified();
            long dstv = dst.lastModified();
            if (dstv >= srcv) {
                L.info(CLINLS.getString("Gather.skip"), dst);
                return;
            }
        }
        dst.getParentFile().mkdirs();
        Files.copy(src, dst);
    }

    /**
     * @option
     */
    String gatherDir = ".gather";

    @Override
    protected void _boot()
            throws Exception {
        if (copyMain == null)
            copyBy(GatherCopy.class);
    }

    class GMap {
        private IFile dstdir;
        private PrefixMap<String> prefixes;
        private Map<File, File> src2dst;

        public GMap(IFile dstdir) {
            assert dstdir != null : "null dstdir";
            this.dstdir = dstdir;
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
            String srcwild = srcdir + "/" + srcfile;
            List<File> srcs = FileWild.listFiles(srcwild);
            if (srcs == null)
                throw new IllegalArgumentException(CLINLS.getString("Gather.srcIsntExisted") + srcwild);
            if (srcs.size() > 1)
                throw new IllegalArgumentException(CLINLS.getString("Gather.tooManyMatchedSrc")
                        + StringArray.join("\n", srcs));
            File src = FilePath.canoniOf(srcs.get(0));
            IFile dst = dstdir.getChild(dstfile);
            src2dst.put(src, dst);
        }

        void gather()
                throws IOException {
            for (Entry<File, File> e : src2dst.entrySet()) {
                File src = e.getKey();
                File dst = e.getValue();
                L.mesg(CLINLS.getString("Gather.get"), src);
                copy(src, dst);
            }
        }

        void deliver()
                throws IOException {
            for (Entry<File, File> e : src2dst.entrySet()) {
                File src = e.getKey();
                File dst = e.getValue();
                L.mesg(CLINLS.getString("Gather.put"), src);
                copy(dst, src);
            }
        }
    }

    @Override
    protected void doFileArgument(IFile dstdir)
            throws Exception {
        if (!dstdir.isTree())
            throw new IllegalArgumentException(CLINLS.getString("Gather.notDirectory") + dstdir);

        IFile gatherd = dstdir.getChild(gatherDir);
        if (!gatherd.isTree())
            throw new IllegalArgumentException(CLINLS.getString("Gather.notGatheredTarget") + dstdir);

        GMap gmap = new GMap(dstdir);
        IFile prefixf = gatherd.getChild(".prefix");
        if (prefixf.exists()) {
            for (String line : prefixf.tooling()._for(StreamReading.class).lines()) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                int eq = line.indexOf('=');
                if (eq == -1)
                    throw new ParseException(CLINLS.getString("Gather.invalidPrefix") + line);
                String prefix = line.substring(0, eq).trim();
                String expanded = line.substring(eq + 1).trim();
                gmap.setPrefix(prefix, expanded);
            }
        }

        for (IFile gfile : gatherd.listChildren()) {
            if (gfile.isTree())
                continue;
            String base = gfile.getName();
            if (base.startsWith("."))
                continue;
            String srcdir = null;
            for (String line : gfile.tooling()._for(StreamReading.class).lines()) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                if (line.endsWith(":")) {
                    srcdir = line.substring(0, line.length() - 1);
                    continue;
                }
                if (srcdir == null)
                    throw new IllegalStateException(CLINLS.getString("Gather.srcdirIsntSet") + gfile + ": \n" + line);
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

    public static void main(String[] args)
            throws Exception {
        new Gather().execute(args);
    }

}
