package net.bodz.lapiota.crypt;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.util.ValueType;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.text.typeparsers.HexParser;
import net.bodz.bas.traits.AbstractParser;
import net.bodz.lapiota.crypt.Hashes.PeekDigest;
import net.bodz.lapiota.nls.CLINLS;

/**
 * Find which part of file make a specific hash value
 */
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class FindHash
        extends BasicCLI {

    public static class Range {

        public int from;
        public int to;

        public Range(int from, int to) {
            if (from >= to)
                throw new IllegalArgumentException(//
                        CLINLS.getString("FindHash.0") + from + " > to " + to); //$NON-NLS-1$ //$NON-NLS-2$
            this.from = from;
            this.to = to;
        }

    }

    public static class RangeParser
            extends AbstractParser<Range> {

        @Override
        public Range parse(String text)
                throws ParseException {
            int comma = text.indexOf(',');
            int from = Integer.parseInt(text.substring(0, comma));
            int to = Integer.parseInt(text.substring(comma + 1));
            return new Range(from, to);
        }

    }

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
    @ValueType(byte[].class)
    @ParseBy(HexParser.class)
    byte[][] hashes;

    /**
     * Default is full file
     *
     * @option -r --range =FROM,TO
     */
    @ValueType(Range.class)
    @ParseBy(RangeParser.class)
    Range[] ranges;

    @Override
    protected void _boot()
            throws Exception {
        if (digest == null)
            digest = MessageDigest.getInstance("CRC32"); //$NON-NLS-1$
        if (!(digest instanceof Cloneable))
            throw new UnsupportedOperationException(
                    CLINLS.getString("FindHash.algIsntClonable") + digest + ", class of " //$NON-NLS-1$ //$NON-NLS-2$
                            + digest.getClass().getName());
        if (hashes == null || hashes.length == 0)
            throw new IllegalUsageException(CLINLS.getString("FindHash.noHash")); //$NON-NLS-1$
    }

    class FindContext {
        byte[] data;
        Range[] ranges;
        // int[] rangeVal;
        MessageDigest cont;
        int count;

        public FindContext(byte[] data, Range[] ranges, MessageDigest cont) {
            this.data = data;
            this.ranges = ranges;
            // this.rangeVal = new int[ranges.length];
            this.cont = cont;
        }

        public void find() {
            try {
                find("", 0, (MessageDigest) cont.clone()); //$NON-NLS-1$
            } catch (CloneNotSupportedException e) {
                throw new UnexpectedException(e);
            }
        }

        void find(String rangesPrefix, int rangeIndex, MessageDigest cont)
                throws CloneNotSupportedException {
            Range range = ranges[rangeIndex];
            // wordSize = BYTE
            int nextRange = rangeIndex + 1;
            for (int from = range.from; from < range.to; from++) {
                MessageDigest cont2 = (MessageDigest) cont.clone();
                PeekDigest peekable = null;
                if (cont2 instanceof PeekDigest)
                    peekable = (PeekDigest) cont2;

                for (int to = from; to <= range.to; to++) {
                    if (rangeIndex != ranges.length - 1) {
                        find(rangesPrefix + "." + from + "-" + to, nextRange, //$NON-NLS-1$ //$NON-NLS-2$
                                (MessageDigest) cont2.clone());
                    } else {
                        byte[] digest;
                        if (peekable != null)
                            digest = peekable.peekDigest();
                        else {
                            digest = ((MessageDigest) cont2.clone()).digest();
                        }

                        boolean matched = match(digest);
                        if ((++count % 1000) == 0 || matched) {
                            String rt = rangesPrefix + "." + from + "-" + to; //$NON-NLS-1$ //$NON-NLS-2$
                            L.tmesg(CLINLS.getString("FindHash.range"), rt, " = ", HEX.encode(digest)); //$NON-NLS-1$ //$NON-NLS-2$
                            if (matched)
                                L.mesg(CLINLS.getString("FindHash.matched")); //$NON-NLS-1$
                        }
                    }
                    if (to != range.to) {
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

    @Override
    protected void doFileArgument(File file)
            throws Exception {
        byte[] data = Files.readBytes(file);
        Range[] ranges = this.ranges;
        if (ranges == null)
            ranges = new Range[] { new Range(0, data.length), };
        new FindContext(data, ranges, digest).find();
    }

    @Override
    protected void _help(IPrintOut out)
            throws CLIException {
        super._help(out);
        out.println();

        out.println(CLINLS.getString("FindHash.algorithms")); //$NON-NLS-1$
        for (String alg : Security.getAlgorithms("MessageDigest")) { //$NON-NLS-1$
            try {
                MessageDigest digest = MessageDigest.getInstance(alg);
                int len = digest.getDigestLength();
                // Provider provider = digest.getProvider();
                out.printf(CLINLS.getString("FindHash.digestInfo_sds"), alg, len, digest //$NON-NLS-1$
                        .getClass());
            } catch (NoSuchAlgorithmException e) {
                out.println(CLINLS.getString("FindHash.noAlg") + alg); //$NON-NLS-1$
            }
        }

        out.flush();
    }

    public static void main(String[] args)
            throws Exception {
        new FindHash().run(args);
    }

}
