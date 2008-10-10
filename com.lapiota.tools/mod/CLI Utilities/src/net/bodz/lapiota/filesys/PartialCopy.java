package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.IntMath;
import net.bodz.bas.lang.err.IllegalUsageError;
import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.bas.mem.AccessException;
import net.bodz.bas.mem.ArrayMemory;
import net.bodz.bas.mem.Memory;
import net.bodz.bas.mem.RandomAccessFileMemory;
import net.bodz.bas.types.TypeParsers.HexParser;
import net.bodz.lapiota.a.ProgramName;
import net.bodz.lapiota.util.StringUtil;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("Copy files by parts")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@ProgramName("partcp")
public class PartialCopy extends BasicCLI {

    @Option(vnam = "NUM", doc = "block size, default 4096")
    protected int     blockSize = 4096;

    @Option(alias = "c", vnam = "CHARSET", doc = "text encoding")
    protected Charset encoding  = Charset.defaultCharset();

    @Option(alias = "p", vnam = "reused padding chars")
    protected char[]  padding   = "\0".toCharArray();

    @Option(alias = "k", doc = "don't expand the dst file")
    protected boolean truncate;

    private Memory    src;
    private Memory    dst;
    private long      srcl;
    private long      dstl;

    private long      srcStart;
    private long      dstStart;
    private long      copySize  = -1;

    private int       copies;

    @Option(name = "src-text", alias = "a", vnam = "TEXT", doc = "src by plain text")
    protected void setSrcText(String text) {
        if (src != null)
            throw new IllegalStateException("src is already set: " + src);
        setSrcBytes(text.getBytes(encoding));
    }

    @Option(name = "src-bytes", alias = "b", vnam = "HEXSTR", parser = HexParser.class, doc = "src by hex bytes")
    protected void setSrcBytes(byte[] bytes) {
        if (src != null)
            throw new IllegalStateException("src is already set: " + src);
        src = new ArrayMemory(bytes);
        srcl = bytes.length;
    }

    @Option(name = "src-file", alias = "f", vnam = "FILE")
    protected void setSrcFile(File file) throws IOException {
        if (src != null)
            throw new IllegalStateException("src is already set: " + src);
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        src = new RandomAccessFileMemory(rf, 0);
        srcl = rf.length();
    }

    @Option(name = "dst-file", alias = "o", vnam = "FILE", doc = "default output to stdout")
    protected void setDstFile(File file) throws IOException {
        if (dst != null)
            throw new IllegalStateException("dst is already set: " + src);
        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        dst = new RandomAccessFileMemory(rf, 0);
        dstl = rf.length();
    }

    @Option(name = "src-start", alias = "x", vnam = "EXP", doc = "start position of src file to copy")
    protected void setSrcStart(String exp) throws AccessException {
        srcStart = parsePosition(src, 0, srcl, exp);
    }

    @Option(name = "src-end", alias = "y", vnam = "EXP", doc = "select size by src range")
    protected void setSrcEnd(String exp) throws AccessException {
        long srcEnd = parsePosition(src, srcStart, srcl, exp);
        copySize = srcEnd - srcStart;
    }

    @Option(name = "dst-start", alias = "z", vnam = "EXP", doc = "start position of dst file to copy to")
    protected void setDstStart(String exp) throws AccessException {
        if (dst == null)
            throw new IllegalStateException("dst isn't set");
        dstStart = parsePosition(dst, 0, dstl, exp);
    }

    @Option(name = "dst-end", alias = "w", vnam = "EXP", doc = "select size by dst range")
    protected void setDstEnd(String exp) throws AccessException {
        if (dst == null)
            throw new IllegalStateException("dst isn't set");
        long dstEnd = parsePosition(dst, dstStart, dstl, exp);
        copySize = dstEnd - dstStart;
    }

    @Option(name = "length", alias = "l", vnam = "LEN", doc = "count of bytes to copy")
    protected void setLength(String exp) {
        copySize = StringUtil.parseLong(exp);
    }

    @Option(alias = "d", doc = "do the copy")
    protected void doCopy() throws AccessException, IOException {
        File tmp = null;
        if (src == null)
            throw new IllegalUsageError("src isn't specified");
        if (dst == null) {
            tmp = File.createTempFile("partcp", ".bin", Files.getTmpDir());
            setDstFile(tmp);
        }

        long src0 = srcl - srcStart;
        long dst0 = dstl - dstStart;
        if (copySize == -1)
            copySize = src0;
        byte[] block = new byte[blockSize];
        long cplen = copySize;
        long padlen = 0;
        if (truncate) {
            if (cplen > dst0)
                cplen = dst0;
        }
        if (cplen > src0) {
            padlen += src0 - cplen;
            cplen = src0;
        }
        Memory srcCopy = src.offset(srcStart);
        Memory dstCopy = dst.offset(dstStart);
        while (cplen > 0) {
            int cb = (int) Math.min(cplen, block.length);
            srcCopy.read(0, block, 0, cb);
            dstCopy.write(0, block, 0, cb);
            srcCopy = srcCopy.offset(cb);
            dstCopy = dstCopy.offset(cb);
            cplen -= cb;
        }
        assert cplen == 0 : "cplen=" + cplen;
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
                    flags = "";
                    break;
                case '-':
                    flags = flags.substring(1);
                    extent = StringUtil.parseInt(flags);
                    fromEnd = true;
                    flags = "";
                    break;
                default:
                    throw new IllegalArgumentException("unknown flag: " + flags);
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
        if ("*".equals(exp)) // till-end
            return -1;

        if (exp.startsWith("/")) { // /REGEX
            Flags flags = new Flags();
            int slash = exp.lastIndexOf('/');
            if (slash != -1) {
                flags.parse(exp.substring(slash + 1));
                exp = exp.substring(0, slash);
            }
            throw new NotImplementedException("REGEX");

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
                throw new IllegalArgumentException("pattern is empty");
            byte[] buf = new byte[pattern.length];
            Memory fMem = mem.offset(start);
            int fSize = IntMath.min(Integer.MAX_VALUE, end - pattern.length);
            for (int off = 0; off < fSize; off++) {
                fMem.read(off, buf);
                if (Arrays.equals(pattern, buf))
                    return start + off + flags.extend(pattern.length);
            }
            throw new RuntimeException("no match of hex");
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

        out.println("Format of EXP: ");
        out.println("    INT             absolute position");
        out.println("   +INT             length");
        out.println("   -INT             offset to the end");
        out.println("   /REGEX[/FLAGS]   search regular expression");
        out.println("  x/HEX/FLAGS       search binary");
        out.println("       flag +/-NUM  extent to the start of match");
        out.println("       flag i       case-insensitive");
        out.println("    *               set to the default value");
    }

    @Override
    protected void _main(String[] args) throws Throwable {
        if (copies == 0)
            doCopy();
    }

    public static void main(String[] args) throws Throwable {
        new PartialCopy().run(args);
    }

}
