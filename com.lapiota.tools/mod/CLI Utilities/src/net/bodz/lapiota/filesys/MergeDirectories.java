package net.bodz.lapiota.filesys;

import static net.bodz.bas.types.util.ArrayOps.Bytes;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.util.ProtectedShell;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.Files;
import net.bodz.lapiota.annotations.ProgramName;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("Merge directories of same architecture")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@ProgramName("dirmerge")
public class MergeDirectories extends BatchProcessCLI {

    @Option(alias = "M", vnam = "ALGORITHM", doc = "message digest algorithm to use, default SHA-1")
    MessageDigest digest;

    @Option(alias = "k", doc = "remove empty directories after delete or move")
    boolean       removeEmptiesDirectories;

    @Option(alias = "t", doc = "if files in same got ratio>threshold, then ignore/delete the different")
    Float         threshold;

    @Option(alias = "T", doc = "delete the files ignored by threshold, implied -t option")
    Float         thresholdAndDeleteIgnored;
    boolean       deleteIgnored;

    @Override
    protected void _boot() throws Throwable {
        if (digest == null)
            digest = MessageDigest.getInstance("SHA-1");
        if (thresholdAndDeleteIgnored != null) {
            assert threshold == null : "both -t and -T are specified";
            threshold = thresholdAndDeleteIgnored;
            deleteIgnored = true;
        }
        if (threshold == null)
            threshold = 1.0f;
    }

    @Override
    protected ProtectedShell _getShell() {
        return new ProtectedShell(!dryRun, L) {

            void mkdirParents(File f) {
                f = Files.canoniOf(f);
                File parent = f.getParentFile();
                if (parent != null)
                    parent.mkdirs();
            }

            void rmdirParents(File f) {
                f = Files.canoniOf(f);
                while ((f = f.getParentFile()) != null)
                    if (!f.delete())
                        break;
            }

            @Override
            public boolean delete(File f) {
                try {
                    return super.delete(f);
                } finally {
                    rmdirParents(f);
                }
            }

            @Override
            public boolean move(File f, File dst) throws IOException {
                try {
                    mkdirParents(dst);
                    return super.move(f, dst);
                } finally {
                    rmdirParents(f);
                }
            }

            @Override
            public boolean move(File f, File dst, boolean force)
                    throws IOException {
                try {
                    mkdirParents(dst);
                    return super.move(f, dst, force);
                } finally {
                    rmdirParents(f);
                }
            }

            @Override
            public boolean renameTo(File f, File dst) {
                try {
                    return super.renameTo(f, dst);
                } finally {
                    rmdirParents(f);
                }
            }
        };
    }

    @Override
    protected void doFileArgument(File startFile) throws Throwable {
        if (!startFile.isDirectory()) {
            L.i.P("skipped file ", startFile);
            return;
        }
        // throw new IllegalArgumentException("not a directory: " + startFile);
        super.doFileArgument(startFile);
    }

    /** hash code of relative file names */
    Map<String, Object> relatives = new HashMap<String, Object>();

    /** hash code of absolute file names, or hash dist */
    Map<File, Object>   absolutes = new HashMap<File, Object>();

    /** hash -> count */
    static class HashDist extends HashMap<Object, Integer> {
        private static final long serialVersionUID = -526743937079608053L;

        int getDist(Object hash) {
            Integer n = get(hash);
            return n == null ? 0 : n;
        }

        void add(Object hash) {
            int newDist = getDist(hash) + 1;
            put(hash, newDist);
        }

        boolean reduced;
        float   rank;
        boolean loose;
        Object  reducedHash = null;

        Object reduce(float threshold) {
            if (reduced)
                return reducedHash;
            int size = size();
            if (threshold >= 1.0f) {
                reducedHash = size == 1 ? keySet().iterator().next() : null;
            } else {
                float max = 0;
                Object maxhash = null;
                int all = 0;
                for (int v : values())
                    all += v;
                assert all != 0;
                for (Entry<Object, Integer> e : entrySet()) {
                    float n = e.getValue();
                    float ratio = n / all;
                    if (ratio > max) {
                        max = ratio;
                        maxhash = e.getKey();
                    }
                }
                if (max > threshold) {
                    reducedHash = maxhash;
                    rank = max;
                    if (max < 1.0f)
                        loose = true;
                }
            }
            reduced = true;
            return reducedHash;
        }
    }

    @Override
    protected ProcessResult doFile(File file) throws Throwable {
        String rname = getRelativeName(file);
        Object rhash = relatives.get(rname);

        if (stage == CALC_DIGEST) {
            Object hash = getHash(file);
            if (rhash == null)
                rhash = hash;
            else if (rhash instanceof HashDist) {
                HashDist dist = (HashDist) rhash;
                dist.add(hash);
            } else {
                HashDist dist = new HashDist();
                dist.add(rhash);
                dist.add(hash);
                rhash = dist;
            }
            relatives.put(rname, rhash);
            absolutes.put(file, hash);
            return null;

        } else if (stage == APPLY_MERGE) {
            Object hash = absolutes.get(file);
            boolean reduced = false;
            if (rhash instanceof HashDist) {
                HashDist dist = (HashDist) rhash;
                rhash = dist.reduce(threshold);
                if (rhash == null) // ambiguous state
                    return null;
                reduced = true;
            }

            if (rhash.equals(hash)) {
                File start = currentStartFile.getParentFile();
                if (start == null)
                    L.w.P("start file ", currentStartFile,
                            " is a rootdir, which may cause problems");
                File dst = getOutputFile(rname, start);
                if (dst.exists()) {
                    if (dst.equals(file))
                        return ProcessResult.pass("same");
                    else
                        return ProcessResult.rm("same-kill"); //psh.delete(file)
                    // ;
                } else
                    return ProcessResult.mv(dst); // psh.move(file, dst);
            } else {
                if (reduced && deleteIgnored)
                    return ProcessResult.rm("ignore-kill");
            }
            return ProcessResult.pass();
        } else {
            throw new IllegalStateException();
        }
    }

    Object getHash(File file) {
        digest.reset();
        for (byte[] block : Files.readByBlock(file))
            digest.update(block);
        byte[] d = digest.digest();
        return Bytes.contents(d);
    }

    static final int CALC_DIGEST = 0;
    static final int APPLY_MERGE = 1;
    private int      stage;

    @Override
    protected boolean doMain(String[] args) throws Throwable {
        stage = CALC_DIGEST;
        super.doMain(args);

        stage = APPLY_MERGE;
        super.doMain(args);

        return false;
    }

    public static void main(String[] args) throws Throwable {
        new MergeDirectories().run(args);
    }

}
