package net.bodz.lapiota.programs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.functors.lang.ControlBreak;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.lapiota.util.BasicCLI;
import net.bodz.lapiota.util.ProgramName;
import net.bodz.lapiota.util.StringUtil;

@Doc("Copy parts of file")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("partcp")
public class PartialCopy extends BasicCLI {

    @Option(alias = "p", vnam = "reused padding chars")
    protected char[]         padding   = "\0".toCharArray();

    private RandomAccessFile src;
    private RandomAccessFile dst;

    private long             srcStart;
    private long             dstStart;
    private long             srcLength = -1;
    private long             dstLength = -1;

    private int              copies;

    @Option(name = "src-file", alias = "f", vnam = "FILE", required = true)
    protected void setSrcFile(File file) throws FileNotFoundException {
        src = new RandomAccessFile(file, "r");
    }

    @Option(name = "dst-file", alias = "F", vnam = "FILE")
    protected void setDstFile(File file) throws FileNotFoundException {
        dst = new RandomAccessFile(file, "rw");
    }

    @Option(name = "src-start", alias = "s", vnam = "EXP", doc = "start position of src file to copy")
    protected void setSrcStart(String exp) throws IOException {
        srcStart = getFilePosition(src, 0, exp);
    }

    @Option(name = "dst-start", alias = "S", vnam = "EXP", doc = "start position of dst file to copy to")
    protected void setDstStart(String exp) throws IOException {
        dstStart = getFilePosition(dst, 0, exp);
    }

    @Option(name = "src-length", alias = "l", vnam = "LEN", doc = "start position of src file to copy")
    protected void setSrcLength(String exp) {
        srcLength = StringUtil.parseLong(exp);
    }

    @Option(name = "dst-length", alias = "L", vnam = "LEN", doc = "start position of dst file to copy to")
    protected void setDstLength(String exp) {
        dstLength = StringUtil.parseLong(exp);
    }

    @Option(name = "src-end", alias = "e", vnam = "EXP", doc = "end position of src file to copy")
    protected void setSrcEnd(String exp) throws IOException {
        long srcEnd = getFilePosition(src, srcStart, exp);
        srcLength = srcEnd - srcStart;
    }

    @Option(name = "dst-end", alias = "E", vnam = "EXP", doc = "end position of dst file to copy to")
    protected void setDstEnd(String exp) throws IOException {
        long dstEnd = getFilePosition(src, dstStart, exp);
        dstLength = dstEnd - dstStart;
    }

    @Option(alias = "c", doc = "do the copy")
    protected void copy() throws IOException {
        File tmp = null;
        if (dst == null) {
            tmp = File.createTempFile("partcp", ".bin", Files.getTmpDir());
            setDstFile(tmp);
        }

        if (srcLength == -1)
            srcLength = src.length() - srcStart;
        if (dstLength == -1)
            dstLength = srcLength;
        byte[] block = new byte[4096];
        long cplen = Math.min(srcLength, dstLength);
        long padlen = dstLength - cplen;
        src.seek(srcStart);
        dst.seek(dstStart);
        while (cplen > 0) {
            int cb = (int) Math.min(cplen, block.length);
            cb = src.read(block, 0, cb);
            if (cb == -1)
                break;
            dst.write(block, 0, cb);
            cplen -= cb;
        }
        if (cplen > 0)
            padlen += cplen;
        if (padlen > 0) {
            for (int i = 0; i < block.length; i++)
                block[i] = (byte) padding[i % padding.length];
            int loopsize = block.length;
            loopsize -= loopsize % padding.length;
            while (padlen > 0) {
                int cb = (int) Math.min(padlen, loopsize);
                dst.write(block, 0, cb);
                padlen -= cb;
            }
        }

        if (tmp != null) {
            dst.close();
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

    protected long getFilePosition(RandomAccessFile file, long start, String exp)
            throws IOException {
        if ("*".equals(exp))
            return -1;
        if (exp.startsWith("/")) {
            Flags flags = new Flags();
            int slash = exp.lastIndexOf('/');
            if (slash != -1) {
                flags.parse(exp.substring(slash + 1));
                exp = exp.substring(0, slash);
            }
            throw new NotImplementedException("REGEX");
        } else if (exp.startsWith("x/")) {
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
            long max = file.length() - pattern.length;
            for (long off = start; off <= max; off++) {
                file.seek(off);
                file.read(buf);
                if (Arrays.equals(pattern, buf))
                    return off + flags.extend(pattern.length);
            }
            throw new RuntimeException("no match of hex");
        }

        long n = StringUtil.parseLong(exp);
        if (exp.startsWith("-"))
            return file.length() + n;
        return n;
    }

    @Override
    protected void _help(CharOut out) throws CLIException {
        try {
            super._help(out);
        } catch (ControlBreak b) {
            out.println();
            out.println("Format of EXP: ");
            out.println("    INT             offset from the beginning");
            out.println("   -INT             offset from the end");
            out.println("   /REGEX[/FLAGS]   search regular expression");
            out.println("  x/HEX/FLAGS       search binary");
            out.println("       flag +/-NUM  extent to the start of match");
            out.println("       flag i       case-insensitive");
            out.println("    *               set to the default value");
            throw b;
        }
    }

    @Override
    protected void _main(String[] args) throws Throwable {
        if (copies == 0)
            copy();
    }

    public static void main(String[] args) throws Throwable {
        new PartialCopy().climain(args);
    }
}
