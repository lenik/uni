package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import net.bodz.bas.c.java.io.TempFile;
import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.c.primitive.IntMath;
import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.cli.plugin.AbstractCLIPlugin;
import net.bodz.bas.cli.plugin.ICLIPlugin;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.io.resource.builtin.LocalFileResource;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.mem.ArrayMemory;
import net.bodz.bas.mem.Memory;
import net.bodz.bas.mem.MemoryAccessException;
import net.bodz.bas.mem.RandomAccessFileMemory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.lapiota.crypt.CRCSum.CRC32pgp;
import net.bodz.lapiota.crypt.FindHash.Range;
import net.bodz.lapiota.util.StringUtil;

/**
 * Copy files by parts
 */
@ProgramName("partcp")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class PartialCopy
        extends BasicCLI {

    /**
     * Block size, default 4096
     *
     * @option =NUM
     */
    protected int blockSize = 4096;

    /**
     * Text encoding
     *
     * @option -c =CHARSET
     */
    protected Charset encoding = Charset.defaultCharset();

    /**
     * Reused padding chars
     *
     * @option -p
     */
    protected char[] padding = tr._("").toCharArray();

    /**
     * Don't expand the dst file
     *
     * @option -k
     */
    protected boolean truncate;

    private Memory src;
    private Memory dst;
    private long srcSize;
    private long dstSize;

    private long srcStart;
    private long dstStart;
    private long srclcopy = -1;
    private long dstlcopy = -1;

    /**
     * Process on src data
     *
     * @option -P =PROCESS=PARAM[,...]
     */
    protected Process process;

    private int copies;

    public PartialCopy() {
        plugins.addCategory(tr._("process"), Process.class);
        plugins.register(tr._("crc.pgp"), PGPCRC32.class, this);
    }

    /**
     * src by ASCIZ text
     *
     * @option -a --src-textsz =TEXT
     */
    protected void setSrcTextSZ(String text) {
        if (src != null)
            throw new IllegalStateException(tr._("src is already set: ") + src);
        byte[] bytes = text.getBytes(encoding);
        bytes = Arrays.append(bytes, (byte) 0); // add terminator.
        setSrcBytes(bytes);
    }

    /**
     * src by raw text
     *
     * @option -A --src-textraw =TEXT
     */
    protected void setSrcTextRaw(String text) {
        if (src != null)
            throw new IllegalStateException(tr._("src is already set: ") + src);
        byte[] bytes = text.getBytes(encoding);
        setSrcBytes(bytes);
    }

    /**
     * src by hex bytes
     *
     * @option -b --src-bytes =HEXSTR
     */
    protected void setSrcBytes(byte[] bytes) {
        if (src != null)
            throw new IllegalStateException(tr._("src is already set: ") + src);
        src = new ArrayMemory(bytes);
        srcSize = bytes.length;
    }

    /**
     * @option -f --src-file =FILE
     */
    protected void setSrcFile(File file)
            throws IOException {
        if (src != null)
            throw new IllegalStateException(tr._("src is already set: ") + src);
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        src = new RandomAccessFileMemory(rf, 0);
        srcSize = rf.length();
    }

    /**
     * Default output to stdout
     *
     * @option -o --dst-file =FILE
     */
    protected void setDstFile(File file)
            throws IOException {
        if (dst != null)
            throw new IllegalStateException(tr._("dst is already set: ") + src);
        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        dst = new RandomAccessFileMemory(rf, 0);
        dstSize = rf.length();
    }

    /**
     * Start position of src file to copy
     *
     * @option -x --src-start =EXP
     */
    protected void setSrcStart(String exp)
            throws MemoryAccessException {
        srcStart = parsePosition(src, 0, srcSize, exp);
    }

    /**
     * Select size by src range
     *
     * @option -y --src-end =EXP
     */
    protected void setSrcEnd(String exp)
            throws MemoryAccessException {
        long srcEnd = parsePosition(src, srcStart, srcSize, exp);
        srclcopy = srcEnd - srcStart;
    }

    /**
     * Start position of dst file to copy to
     *
     * @option -z --dst-start =EXP
     */
    protected void setDstStart(String exp)
            throws MemoryAccessException {
        if (dst == null)
            throw new IllegalStateException(tr._("dst isn\'t set"));
        dstStart = parsePosition(dst, 0, dstSize, exp);
    }

    /**
     * Select size by dst range
     *
     * @option -w --dst-end =EXP
     */
    protected void setDstEnd(String exp)
            throws MemoryAccessException {
        if (dst == null)
            throw new IllegalStateException(tr._("dst isn\'t set"));
        long dstEnd = parsePosition(dst, dstStart, dstSize, exp);
        srclcopy = dstEnd - dstStart;
    }

    /**
     * Count of bytes to copy
     *
     * @option -l --length =LEN
     */
    protected void setLength(String exp) {
        srclcopy = StringUtil.parseLong(exp);
    }

    /**
     * Do the copy
     *
     * @option -d
     */
    protected void doCopy()
            throws MemoryAccessException, IOException {
        File tmp = null;
        if (src == null)
            throw new IllegalUsageException(tr._("src isn\'t specified"));
        if (dst == null) {
            tmp = File.createTempFile("partcp", ".bin", TempFile.getTempRoot());
            setDstFile(tmp);
        }

        long srcRest = srcSize - srcStart;
        long dstRest = dstSize - dstStart;
        long padlen = 0;
        long srcl = srclcopy == -1 ? srcRest : srclcopy;
        long dstl = dstlcopy == -1 ? dstRest : dstlcopy;
        Memory srcCopy = src.offset(srcStart);

        if (process != null) {
            byte[] gen = process.process(srcCopy, srcl);
            srcCopy = new ArrayMemory(gen);
            srcl = gen.length;
            srcRest = gen.length;
        }
        if (truncate) {
            if (srcl > dstRest)
                srcl = dstRest;
        }
        if (srcl > srcRest) {
            padlen += srcRest - srcl;
            srcl = srcRest;
        }
        if (srcl > dstl) {
            padlen += dstl - srcl;
            dstl = srcl;
        }

        Memory dstCopy = dst.offset(dstStart);
        byte[] block = new byte[blockSize];
        while (srcl > 0) {
            int cb = (int) Math.min(srcl, block.length);
            srcCopy.read(0, block, 0, cb);
            dstCopy.write(0, block, 0, cb);
            srcCopy = srcCopy.offset(cb);
            dstCopy = dstCopy.offset(cb);
            srcl -= cb;
        }
        assert srcl == 0 : "srcl=" + srcl;

        if (padlen > 0) {
            for (int i = 0; i < block.length; i++)
                block[i] = (byte) padding[i % padding.length];
            int loopsize = block.length;
            loopsize -= loopsize % padding.length;
            while (padlen > 0) {
                int cb = (int) Math.min(padlen, loopsize);
                dstCopy.write(0, block, 0, cb);
                dstCopy = dstCopy.offset(cb);
                padlen -= cb;
            }
        }

        if (tmp != null) {
            ((RandomAccessFileMemory) dst).getFile().close();
            dst = null;

            LocalFileResource res = new LocalFileResource(tmp);
            byte[] data = res.tooling()._for(StreamReading.class).read();
            System.out.write(data);
            tmp.delete();
        }
        copies++;
    }

    static class Flags {
        boolean _case = true;
        boolean fromEnd = false;
        int extent = 0;

        void parse(String flags) {
            while (flags.length() > 0) {
                switch (flags.charAt(0)) {
                case 'i':
                    _case = false;
                    break;
                case '+':
                    flags = flags.substring(1);
                    extent = StringUtil.parseInt(flags);
                    fromEnd = false;
                    flags = "";
                    break;
                case '-':
                    flags = flags.substring(1);
                    extent = StringUtil.parseInt(flags);
                    fromEnd = true;
                    flags = "";
                    break;
                default:
                    throw new IllegalArgumentException(tr._("unknown flag: ") + flags);
                }
            }
        }

        int extend(int end) {
            if (fromEnd)
                return end + extent;
            return extent;
        }
    }

    protected long parsePosition(Memory mem, long start, long end, String exp)
            throws MemoryAccessException {
        if ("*".equals(exp)) // till-end
            return -1;

        if (exp.startsWith("/")) { // /REGEX
            Flags flags = new Flags();
            int slash = exp.lastIndexOf('/');
            if (slash != -1) {
                flags.parse(exp.substring(slash + 1));
                exp = exp.substring(0, slash);
            }
            throw new NotImplementedException(tr._("REGEX"));

        } else if (exp.startsWith("x/")) { // x/HEX
            Flags flags = new Flags();
            exp = exp.substring(2);
            int slash = exp.lastIndexOf('/');
            if (slash != -1) {
                flags.parse(exp.substring(slash + 1));
                exp = exp.substring(0, slash);
            }
            byte[] pattern = StringUtil.parseHex(exp);
            if (pattern.length == 0)
                throw new IllegalArgumentException(tr._("pattern is empty"));
            byte[] buf = new byte[pattern.length];
            Memory fMem = mem.offset(start);
            int fSize = IntMath.min(Integer.MAX_VALUE, end - pattern.length);
            for (int off = 0; off < fSize; off++) {
                fMem.read(off, buf);
                if (Arrays.equals(pattern, buf))
                    return start + off + flags.extend(pattern.length);
            }
            throw new RuntimeException(tr._("no match of hex"));
        }

        else { // [+-]INT
            int c = exp.charAt(0);
            if (c == '+')
                exp = exp.substring(1);
            long n = StringUtil.parseLong(exp);
            switch (c) {
            case '+':
                return start + n;
            case '-':
                return end + n + 1;
            default:
                return n;
            }
        }
    }

    @Override
    protected void _help(IPrintOut out) {
        super._help(out);
        out.println();

        out.println(tr._("Format of EXP: "));
        out.println(tr._("    INT             absolute position"));
        out.println(tr._("   +INT             length"));
        out.println(tr._("   -INT             offset to the end"));
        out.println(tr._("   /REGEX[/FLAGS]   search regular expression"));
        out.println(tr._("  x/HEX/FLAGS       search binary"));
        out.println(tr._("       flag +/-NUM  extent to the start of match"));
        out.println(tr._("       flag i       case-insensitive"));
        out.println(tr._("    *               set to the default value"));
        out.flush();
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (copies == 0)
            doCopy();
    }

    public static void main(String[] args)
            throws Exception {
        new PartialCopy().execute(args);
    }

    static interface Process
            extends ICLIPlugin {

        byte[] process(Memory src, long len)
                throws MemoryAccessException;

    }

    static abstract class _Process
            extends AbstractCLIPlugin
            implements Process {
    }

    /**
     * PGP CRC32
     */
    class PGPCRC32
            extends _Process {

        /**
         * Specify a relative range in the process region, to fill with the pad-bytes
         *
         * @option =FROM,TO
         */
        Range fillRange;

        /**
         * Bytes to pad
         *
         * @option =HEX
         */
        byte[] padBytes = { 0, 0, 0, 0 };

        public PGPCRC32() {
        }

        @Override
        public byte[] process(Memory src, long lenl)
                throws MemoryAccessException {
            if (lenl >= Integer.MAX_VALUE)
                throw new UnsupportedOperationException(tr._("unsupport to get crc32 from >2G block"));
            int len = (int) lenl;
            byte[] bigEndian = new byte[len];
            src.read(0, bigEndian);
            if (len % 4 != 0)
                throw new IllegalArgumentException(tr._("PGP-CRC32 is DWORD aligned"));
            if (fillRange != null) { // fill before switch byte-order
                int padIndex = 0;
                for (int i = fillRange.from; i < fillRange.to; i++) {
                    byte padByte = padBytes[padIndex];
                    padIndex = (padIndex + 1) % padBytes.length;
                    bigEndian[i] = padByte;
                }
            }
            for (int i = 0; i < len; i += 4) {
                byte x0 = bigEndian[i + 0];
                byte x1 = bigEndian[i + 1];
                bigEndian[i + 0] = bigEndian[i + 3];
                bigEndian[i + 1] = bigEndian[i + 2];
                bigEndian[i + 2] = x1;
                bigEndian[i + 3] = x0;
            }
            CRC32pgp crc32 = new CRC32pgp();
            crc32.update(bigEndian, 0, len);
            byte[] v = crc32.getBytesLE();
            return v;
        }

    }

}
