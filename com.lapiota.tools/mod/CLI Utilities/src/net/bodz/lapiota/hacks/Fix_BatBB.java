package net.bodz.lapiota.hacks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.Files;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("Fix: .bat goto labels must not cross 1k block boundary.")
@Version( { 0, 0 })
@RcsKeywords(id = "$Id: Fix_Bat4K.java 0 2008-8-6 上午05:59:30 lenik $")
public class Fix_BatBB extends BatchProcessCLI {

    @Option(alias = "b", vnam = "SIZE", doc = "block size")
    int  blockSize = 1024;

    @Option
    char fillChar  = ':';

    @Override
    protected ProcessResult doFileEdit(InputStream in, OutputStream out)
            throws Throwable {
        int start = 0; // out

        ByteBuffer lineBuf = ByteBuffer.allocate(1024);
        byte[] buf = new byte[Files.blockSize];
        int cb;
        boolean leading = true;
        int leadChar = 0;

        while ((cb = in.read(buf, 0, buf.length)) != -1) {
            for (int i = 0; i < cb; i++) {
                byte b = buf[i];
                lineBuf.put(b);
                if (b == '\n') {
                    lineBuf.flip();
                    int lineLen = lineBuf.limit();
                    if (leadChar == ':') { // label line
                        int end = start + lineLen; // include EOL
                        if (crossBlocks(start, end)) {
                            int fill = fillUps(out, start);
                            start += fill;
                            end += fill;
                        }
                    }
                    out.write(lineBuf.array(), 0, lineLen);
                    start += lineLen;
                    lineBuf.clear();
                    leading = true;
                    leadChar = 0;
                } else if (leading) {
                    if (!Character.isWhitespace(b)) {
                        leadChar = b;
                        leading = false;
                    }
                }
            }
        }
        return ProcessResult.compareAndSave();
    }

    boolean crossBlocks(int start, int end) {
        int startBlock = start / blockSize;
        int endBlock = end / blockSize;
        return startBlock != endBlock;
    }

    static byte[] remPrefix = ":: Fix_BatBB ::".getBytes();

    int fillUps(OutputStream out, int start) throws IOException {
        int fill = blockSize - start % blockSize;
        int filled = 0;
        while (filled < fill) {
            out.write(remPrefix);
            filled += remPrefix.length;
            int stuff = Math.min(fill - filled - 1, 70);
            if (stuff > 0) {
                filled += stuff;
                while (stuff-- > 0) {
                    out.write(fillChar);
                }
            }
            out.write('\n');
            filled++;
        }
        return filled;
    }

    public static void main(String[] args) throws Throwable {
        new Fix_BatBB().run(args);
    }

}
