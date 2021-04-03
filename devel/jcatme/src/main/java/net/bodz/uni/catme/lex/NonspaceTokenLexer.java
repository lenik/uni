package net.bodz.uni.catme.lex;

import java.io.IOException;

public class NonspaceTokenLexer
        implements
            ITokenLexer<String> {

    static final int _LEN_NSWORD = 32;

    @Override
    public String lex(ILa1CharIn in)
            throws IOException {
        StringBuilder buf = new StringBuilder(_LEN_NSWORD);
        int c;
        while ((c = in.look()) != -1) {
            char ch = (char) c;
            if (Character.isWhitespace(ch))
                break;
            in.read();
            buf.append(ch);
        }
        return buf.toString();
    }

    public static final NonspaceTokenLexer INSTANCE = new NonspaceTokenLexer();

}
