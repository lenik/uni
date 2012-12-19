package net.bodz.lapiota.filesys;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.vfs.IFile;

/**
 * Merge directories of same architecture
 */
@ProgramName("dirmerge")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class MergeDirectories
        extends BatchEditCLI {

    /**
     * Message digest algorithm to use, default SHA-1
     *
     * @option -M =ALGORITHM
     */
    MessageDigest digest;

    /**
     * Remove empty directories after delete or move
     *
     * @option -k
     */
    boolean removeEmptiesDirectories;

    /**
     * If files in same got ratio>threshold, then ignore/delete the different
     *
     * @option -t =THRESHOLD
     */
    Float threshold;

    /**
     * Delete the files ignored by threshold, implied -t option
     *
     * @option -T =THRESHOLD
     */
    Float thresholdAndDeleteIgnored;
    boolean deleteIgnored;

    @Override
    protected void _boot()
            throws Exception {
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

    // TODO Remove empty parents after rm/mv/.. operations.
    static class ClearParents_PSH {
    }

    @Override
    protected void doFileArgument(IFile startFile)
            throws Exception {
        if (!startFile.isDirectory()) {
            logger.info(tr._("skipped file "), startFile);
            return;
        }
        // throw new IllegalArgumentException("not a directory: " + startFile);
        super.doFileArgument(startFile);
    }

    /** hash code of relative file names */
    Map<String, Object> relatives = new HashMap<String, Object>();

    /** hash code of absolute file names, or hash dist */
    Map<IFile, Object> absolutes = new HashMap<IFile, Object>();

    /** hash -> count */
    static class HashDist
            extends HashMap<Object, Integer> {
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
        float rank;
        boolean loose;
        Object reducedHash = null;

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
    protected EditResult doEdit(IFile file)
            throws Exception {
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
                IFile start = currentStartFile.getParentFile();
                if (start == null) {
                    logger.warnf(tr._("start file %s is a root directory,which may be dangerous  "), currentStartFile);
                }
                IFile dst = getOutputFile(rname, start);
                if (dst.exists()) {
                    if (dst.equals(file))
                        return EditResult.pass(tr._("same"));
                    else
                        return EditResult.rm(tr._("same-kill")); // psh.delete(file)
                    // ;
                } else
                    return EditResult.mv(dst); // psh.move(file, dst);
            } else {
                if (reduced && deleteIgnored)
                    return EditResult.rm(tr._("ignore-kill"));
            }
            return EditResult.pass();
        } else {
            throw new IllegalStateException();
        }
    }

    Object getHash(IFile file)
            throws IOException {
        digest.reset();
        for (byte[] block : file.tooling()._for(StreamReading.class).blocks())
            digest.update(block);
        byte[] d = digest.digest();
        return Arrays.hashCode(d);
    }

    static final int CALC_DIGEST = 0;
    static final int APPLY_MERGE = 1;
    private int stage;

    public static void main(String[] args)
            throws Exception {
        new MergeDirectories().execute(args);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        stage = CALC_DIGEST;
        super.mainImpl(args);

        stage = APPLY_MERGE;
        super.mainImpl(args);
    }

}
