package net.bodz.uni.crypt.pgp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

import net.bodz.bas.data.codec.builtin.HexCodec;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.range.IntegerRange;
import net.bodz.bas.vfs.IFile;
import net.bodz.uni.crypt.pgp.Hashes.PeekDigest;

/**
 * Find which part of file make a specific hash value
 */
@MainVersion({ 0, 1 })
@ProgramName("findhash")
@RcsKeywords(id = "$Id$")
public class FindHash
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(FindHash.class);

    /**
     * Hash algorithm to use, default CRC32
     *
     * @option -a --algorithm =ALG
     */
    MessageDigest digest = new Hashes.CRC32_LE();

    /**
     * Hash value to find
     *
     * @option -x --hash =HASH required
     */
    byte[][] hashes;

    /**
     * Default is full file
     *
     * @option -r --range =FROM,TO
     */
    IntegerRange[] ranges;

    @Override
    protected void reconfigure()
            throws Exception {
        if (digest == null)
            digest = MessageDigest.getInstance("CRC32");
        if (!(digest instanceof Cloneable))
            throw new UnsupportedOperationException(nls.tr("The algorithm isn\'t clonable: ") + digest + ", class of "
                    + digest.getClass().getName());
        if (hashes == null || hashes.length == 0)
            throw new IllegalUsageException(nls.tr("no hash specified"));
    }

    class FindContext {
        byte[] data;
        IntegerRange[] ranges;
        // int[] rangeVal;
        MessageDigest cont;
        int count;

        public FindContext(byte[] data, IntegerRange[] ranges, MessageDigest cont) {
            this.data = data;
            this.ranges = ranges;
            // this.rangeVal = new int[ranges.length];
            this.cont = cont;
        }

        public void find() {
            try {
                find("", 0, (MessageDigest) cont.clone());
            } catch (CloneNotSupportedException e) {
                throw new UnexpectedException(e);
            }
        }

        void find(String rangesPrefix, int rangeIndex, MessageDigest cont)
                throws CloneNotSupportedException {
            IntegerRange range = ranges[rangeIndex];
            // wordSize = BYTE
            int nextRange = rangeIndex + 1;
            for (int from = range.start; from < range.end; from++) {
                MessageDigest cont2 = (MessageDigest) cont.clone();
                PeekDigest peekable = null;
                if (cont2 instanceof PeekDigest)
                    peekable = (PeekDigest) cont2;

                for (int to = from; to <= range.end; to++) {
                    if (rangeIndex != ranges.length - 1) {
                        find(rangesPrefix + "." + from + "-" + to, nextRange, (MessageDigest) cont2.clone());
                    } else {
                        byte[] digest;
                        if (peekable != null)
                            digest = peekable.peekDigest();
                        else {
                            digest = ((MessageDigest) cont2.clone()).digest();
                        }

                        boolean matched = match(digest);
                        if ((++count % 1000) == 0 || matched) {
                            String rt = rangesPrefix + "." + from + "-" + to;
                            logger.status(nls.tr("Range: "), rt, " = ", HexCodec.getInstance().encode(digest));
                            if (matched)
                                logger.mesg(nls.tr("Match! "));
                        }
                    }
                    if (to != range.end) {
                        cont2.update(data[to]);
                        // rangeVal[rangeIndex] = to;
                    }
                }
            }
        }

        protected boolean match(byte[] digest) {
            for (int h = 0; h < hashes.length; h++) {
                byte[] hash = hashes[h];
                assert digest.length == hash.length;
                if (Arrays.equals(digest, hash))
                    return true;
            }
            return false;
        }

    }

    public void findFileRangeForHash(IFile file)
            throws Exception {
        byte[] data = file.to(StreamReading.class).read();
        IntegerRange[] ranges = this.ranges;
        if (ranges == null)
            ranges = new IntegerRange[] { new IntegerRange(0, data.length), };
        new FindContext(data, ranges, digest).find();
    }

    @Override
    protected void showHelpPage(IPrintOut out) {
        super.showHelpPage(out);
        out.println();

        out.println(nls.tr("Algorithms: "));
        for (String alg : Security.getAlgorithms("MessageDigest")) {
            try {
                MessageDigest digest = MessageDigest.getInstance(alg);
                int len = digest.getDigestLength();
                // Provider provider = digest.getProvider();
                out.printf(nls.tr("    %16s: len=%d, %s\n"), alg, len, digest.getClass());
            } catch (NoSuchAlgorithmException e) {
                out.println(nls.tr("    Err: ") + alg);
            }
        }

        out.flush();
    }

    public static void main(String[] args)
            throws Exception {
        new FindHash().execute(args);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (IFile file : expandFiles(args))
            findFileRangeForHash(file);
    }

}
