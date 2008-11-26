package net.bodz.lapiota.crypt;

import static net.bodz.bas.text.encodings.Encodings.HEX;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.IllegalUsageException;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.lang.err.UnexpectedException;
import net.bodz.bas.types.TypeParser;
import net.bodz.bas.types.TypeParsers.HexParser;
import net.bodz.lapiota.crypt.Hashes.PeekDigest;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("Find which part of file make a specific hash value")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class FindHash extends BasicCLI {

    public static class Range {

        public int from;
        public int to;

        public Range(int from, int to) {
            if (from >= to)
                throw new IllegalArgumentException(//
                        "from " + from + " > to " + to);
            this.from = from;
            this.to = to;
        }

    }

    public static class RangeParser implements TypeParser {

        @Override
        public Object parse(String text) throws ParseException {
            int comma = text.indexOf(',');
            int from = Integer.parseInt(text.substring(0, comma));
            int to = Integer.parseInt(text.substring(comma + 1));
            return new Range(from, to);
        }

    }

    @Option(name = "algorithm", alias = "a", vnam = "ALG", doc = "hash algorithm to use, default CRC32")
    MessageDigest digest = new Hashes.CRC32_LE();

    @Option(name = "hash", alias = "x", vnam = "HASH", valtype = byte[].class, required = true, doc = "hash value to find")
    @ParseBy(HexParser.class)
    byte[][]      hashes;

    @Option(name = "range", alias = "r", vnam = "FROM,TO", valtype = Range.class, doc = "default is full file")
    @ParseBy(RangeParser.class)
    Range[]       ranges;

    @Override
    protected void _boot() throws Throwable {
        if (digest == null)
            digest = MessageDigest.getInstance("CRC32");
        if (!(digest instanceof Cloneable))
            throw new UnsupportedOperationException(
                    "The algorithm isn't clonable: " + digest + ", class of "
                            + digest.getClass().getName());
        if (hashes == null || hashes.length == 0)
            throw new IllegalUsageException("no hash specified");
    }

    class FindContext {
        byte[]        data;
        Range[]       ranges;
        // int[] rangeVal;
        MessageDigest cont;
        int           count;

        public FindContext(byte[] data, Range[] ranges, MessageDigest cont) {
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
                        find(rangesPrefix + "." + from + "-" + to, nextRange,
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
                            String rt = rangesPrefix + "." + from + "-" + to;
                            L.m.sig("Range: ", rt, " = ", HEX.encode(digest));
                            if (matched)
                                L.m.P("Match! ");
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
    protected void doFileArgument(File file) throws Throwable {
        byte[] data = Files.readBytes(file);
        Range[] ranges = this.ranges;
        if (ranges == null)
            ranges = new Range[] { new Range(0, data.length), };
        new FindContext(data, ranges, digest).find();
    }

    @Override
    protected void _help(CharOut out) throws CLIException {
        super._help(out);
        out.println();

        out.println("Algorithms: ");
        for (String alg : Security.getAlgorithms("MessageDigest")) {
            try {
                MessageDigest digest = MessageDigest.getInstance(alg);
                int len = digest.getDigestLength();
                // Provider provider = digest.getProvider();
                out.printf("    %16s: len=%d, %s\n", alg, len, digest
                        .getClass());
            } catch (NoSuchAlgorithmException e) {
                out.println("    Err: " + alg);
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        new FindHash().run(args);
    }

}
