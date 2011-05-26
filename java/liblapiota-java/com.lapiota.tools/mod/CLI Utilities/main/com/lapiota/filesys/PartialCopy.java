package com.lapiota.filesys;

import static net.bodz.bas.types.util.ArrayOps.Bytes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.lapiota.crypt.CRCSum.CRC32pgp;
import com.lapiota.crypt.FindHash.Range;
import com.lapiota.crypt.FindHash.RangeParser;
import com.lapiota.nls.CLINLS;
import com.lapiota.util.StringUtil;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.IntMath;
import net.bodz.bas.lang.err.IllegalUsageException;
import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.bas.mem.AccessException;
import net.bodz.bas.mem.ArrayMemory;
import net.bodz.bas.mem.Memory;
import net.bodz.bas.mem.RandomAccessFileMemory;
import net.bodz.bas.types.parsers.HexParser;

@Doc("Copy files by parts")
@ProgramName("partcp")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class PartialCopy extends BasicCLI {

    @Option(vnam = "NUM", doc = "block size, default 4096")
    protected int     blockSize = 4096;

    @Option(alias = "c", vnam = "CHARSET", doc = "text encoding")
    protected Charset encoding  = Charset.defaultCharset();

    @Option(alias = "p", vnam = "reused padding chars")
    protected char[]  padding   = CLINLS.getString("PartialCopy.0").toCharArray(); //$NON-NLS-1$

    @Option(alias = "k", doc = "don't expand the dst file")
    protected boolean truncate;

    private Memory    src;
    private Memory    dst;
    private long      srcSize;
    private long      dstSize;

    private long      srcStart;
    private long      dstStart;
    private long      srclcopy  = -1;
    private long      dstlcopy  = -1;

    @Option(alias = "P", vnam = "PROCESS=PARAM[,...]", doc = "process on src data")
    protected Process process;

    private int       copies;

    public PartialCopy() {
        plugins.registerCategory(CLINLS.getString("PartialCopy.1"), Process.class); //$NON-NLS-1$
        plugins.register(CLINLS.getString("PartialCopy.2"), PGPCRC32.class, this); //$NON-NLS-1$
    }

    @Option(name = "src-textsz", alias = "a", vnam = "TEXT", doc = "src by ASCIZ text")
    protected void setSrcTextSZ(String text) {
        if (src != null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.srcIsSet") + src); //$NON-NLS-1$
        byte[] bytes = text.getBytes(encoding);
        bytes = Bytes.copyOf(bytes, bytes.length + 1);
        setSrcBytes(bytes);
    }

    @Option(name = "src-textraw", alias = "A", vnam = "TEXT", doc = "src by raw text")
    protected void setSrcTextRaw(String text) {
        if (src != null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.srcIsSet") + src); //$NON-NLS-1$
        byte[] bytes = text.getBytes(encoding);
        setSrcBytes(bytes);
    }

    @Option(name = "src-bytes", alias = "b", vnam = "HEXSTR", doc = "src by hex bytes")
    @ParseBy(HexParser.class)
    protected void setSrcBytes(byte[] bytes) {
        if (src != null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.srcIsSet") + src); //$NON-NLS-1$
        src = new ArrayMemory(bytes);
        srcSize = bytes.length;
    }

    @Option(name = "src-file", alias = "f", vnam = "FILE")
    protected void setSrcFile(File file) throws IOException {
        if (src != null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.srcIsSet") + src); //$NON-NLS-1$
        RandomAccessFile rf = new RandomAccessFile(file, "r"); //$NON-NLS-1$
        src = new RandomAccessFileMemory(rf, 0);
        srcSize = rf.length();
    }

    @Option(name = "dst-file", alias = "o", vnam = "FILE", doc = "default output to stdout")
    protected void setDstFile(File file) throws IOException {
        if (dst != null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.dstIsSet") + src); //$NON-NLS-1$
        RandomAccessFile rf = new RandomAccessFile(file, "rw"); //$NON-NLS-1$
        dst = new RandomAccessFileMemory(rf, 0);
        dstSize = rf.length();
    }

    @Option(name = "src-start", alias = "x", vnam = "EXP", doc = "start position of src file to copy")
    protected void setSrcStart(String exp) throws AccessException {
        srcStart = parsePosition(src, 0, srcSize, exp);
    }

    @Option(name = "src-end", alias = "y", vnam = "EXP", doc = "select size by src range")
    protected void setSrcEnd(String exp) throws AccessException {
        long srcEnd = parsePosition(src, srcStart, srcSize, exp);
        srclcopy = srcEnd - srcStart;
    }

    @Option(name = "dst-start", alias = "z", vnam = "EXP", doc = "start position of dst file to copy to")
    protected void setDstStart(String exp) throws AccessException {
        if (dst == null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.dstIsntSet")); //$NON-NLS-1$
        dstStart = parsePosition(dst, 0, dstSize, exp);
    }

    @Option(name = "dst-end", alias = "w", vnam = "EXP", doc = "select size by dst range")
    protected void setDstEnd(String exp) throws AccessException {
        if (dst == null)
            throw new IllegalStateException(CLINLS.getString("PartialCopy.dstIsntSet")); //$NON-NLS-1$
        long dstEnd = parsePosition(dst, dstStart, dstSize, exp);
        srclcopy = dstEnd - dstStart;
    }

    @Option(name = "length", alias = "l", vnam = "LEN", doc = "count of bytes to copy")
    protected void setLength(String exp) {
        srclcopy = StringUtil.parseLong(exp);
    }

    @Option(alias = "d", doc = "do the copy")
    protected void doCopy() throws AccessException, IOException {
        File tmp = null;
        if (src == null)
            throw new IllegalUsageException(CLINLS.getString("PartialCopy.srcIsntSpecified")); //$NON-NLS-1$
        if (dst == null) {
            tmp = File.createTempFile("partcp", ".bin", Files.getTmpDir()); //$NON-NLS-1$ //$NON-NLS-2$
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
        assert srcl == 0 : "srcl=" + srcl; //$NON-NLS-1$

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
            byte[] data = Files.readBytes(tmp);
            System.out.write(data);
            tmp.delete();
        }
        copies++;
    }

    static class Flags {
        boolean _case   = true;
        boolean fromEnd = false;
        int     extent  = 0;

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
                    flags = ""; //$NON-NLS-1$
                    break;
                case '-':
                    flags = flags.substring(1);
                    extent = StringUtil.parseInt(flags);
                    fromEnd = true;
                    flags = ""; //$NON-NLS-1$
                    break;
                default:
                    throw new IllegalArgumentException(
                            CLINLS.getString("PartialCopy.unknownFlag") + flags); //$NON-NLS-1$
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
            throws AccessException {
        if ("*".equals(exp)) // till-end //$NON-NLS-1$
            return -1;

        if (exp.startsWith("/")) { // /REGEX //$NON-NLS-1$
            Flags flags = new Flags();
            int slash = exp.lastIndexOf('/');
            if (slash != -1) {
                flags.parse(exp.substring(slash + 1));
                exp = exp.substring(0, slash);
            }
            throw new NotImplementedException(CLINLS.getString("PartialCopy.REGEX")); //$NON-NLS-1$

        } else if (exp.startsWith("x/")) { // x/HEX //$NON-NLS-1$
            Flags flags = new Flags();
            exp = exp.substring(2);
            int slash = exp.lastIndexOf('/');
            if (slash != -1) {
                flags.parse(exp.substring(slash + 1));
                exp = exp.substring(0, slash);
            }
            byte[] pattern = StringUtil.parseHex(exp);
            if (pattern.length == 0)
                throw new IllegalArgumentException(CLINLS.getString("PartialCopy.patternIsEmpty")); //$NON-NLS-1$
            byte[] buf = new byte[pattern.length];
            Memory fMem = mem.offset(start);
            int fSize = IntMath.min(Integer.MAX_VALUE, end - pattern.length);
            for (int off = 0; off < fSize; off++) {
                fMem.read(off, buf);
                if (Arrays.equals(pattern, buf))
                    return start + off + flags.extend(pattern.length);
            }
            throw new RuntimeException(CLINLS.getString("PartialCopy.noMatchHex")); //$NON-NLS-1$
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
    protected void _help(CharOut out) throws CLIException {
        super._help(out);
        out.println();

        out.println(CLINLS.getString("PartialCopy.exprFormat")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.absolute")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.addlen")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.sublen")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.regex")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.regex.bin")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.xregex.flagExt")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.xregex.flagI")); //$NON-NLS-1$
        out.println(CLINLS.getString("PartialCopy.expr.setToDef")); //$NON-NLS-1$
        out.flush();
    }

    @Override
    protected void doMain(String[] args) throws Exception {
        if (copies == 0)
            doCopy();
    }

    public static void main(String[] args) throws Exception {
        new PartialCopy().run(args);
    }

    static interface Process extends CLIPlugin {

        byte[] process(Memory src, long len) throws AccessException;

    }

    static abstract class _Process extends _CLIPlugin implements Process {
    }

    @Doc("PGP CRC32")
    class PGPCRC32 extends _Process {

        @Option(vnam = "FROM,TO", doc = "specify a relative range in the process region, to fill with the pad-bytes")
        @ParseBy(RangeParser.class)
        Range  fillRange;

        @Option(vnam = "HEX", doc = "bytes to pad")
        @ParseBy(HexParser.class)
        byte[] padBytes = { 0, 0, 0, 0 };

        public PGPCRC32() {
        }

        @Override
        public byte[] process(Memory src, long lenl) throws AccessException {
            if (lenl >= Integer.MAX_VALUE)
                throw new UnsupportedOperationException(CLINLS.getString("PartialCopy.tooLong")); //$NON-NLS-1$
            int len = (int) lenl;
            byte[] bigEndian = new byte[len];
            src.read(0, bigEndian);
            if (len % 4 != 0)
                throw new IllegalArgumentException(CLINLS
                        .getString("PartialCopy.pgpCrc32ShouldAligned")); //$NON-NLS-1$
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