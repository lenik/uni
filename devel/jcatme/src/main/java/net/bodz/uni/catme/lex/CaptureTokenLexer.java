package net.bodz.uni.catme.lex;

import java.io.IOException;

import net.bodz.bas.err.ParseException;

public class CaptureTokenLexer
        implements
            ITokenLexer<String> {

    @Override
    public String lex(ILa1CharIn in)
            throws IOException, ParseException {
        StringBuilder buf = new StringBuilder();
        int c;
        while ((c = in.read()) != -1)
            buf.append((char) c);
        return buf.toString();
    }

    public static final CaptureTokenLexer INSTANCE = new CaptureTokenLexer();

}
