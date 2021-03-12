package net.bodz.uni.shelj.c.builtin;

import java.nio.CharBuffer;

import net.bodz.bas.c.java.util.regex.BufParsers;
import net.bodz.bas.c.java.util.regex.Unescape;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BatchEditCLI;
import net.bodz.bas.program.skel.FileHandler;

/**
 * Convert ascii chars or \\uNNNN to native chars
 */
@MainVersion({ 0, 1 })
@ProgramName("a2native")
@RcsKeywords(id = "$Id$")
public class Ascii2Native
        extends BatchEditCLI {

    @Override
    protected void reconfigure()
            throws Exception {
        // default inputEncoding == native(includes ascii)
        // default outputEncoding == native
    }

    @Override
    public void processFile(FileHandler handler)
            throws Exception {
        IPrintOut out = handler.openPrintOut();

        UxxxxDecoder decoder = new UxxxxDecoder();
        for (String line : handler.read().lines()) {
            line = decoder.process(line);
            out.println(line);
        }
        handler.save();
    }

    public static void main(String[] args)
            throws Exception {
        new Ascii2Native().execute(args);
    }

}

/**
 * Convert \\uXXXX to unicode char.
 */
class UxxxxDecoder
        extends Unescape {

    public UxxxxDecoder() {
        super("\\");
    }

    @Override
    protected String decode(CharBuffer in) {
        char c = in.get();
        int v;
        switch (c) {
        case 'u':
            // Character.MAX_CODE_POINT;
            v = BufParsers.getInt(in, 16, Character.MAX_VALUE);
            if (v != -1)
                c = (char) v;
            break;
        }
        return String.valueOf(c);
    }

}
