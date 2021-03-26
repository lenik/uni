package net.bodz.uni.catme.lex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.err.ParseException;

public class LinkedLexer
        implements
            ITokenLexer<List<?>> {

    List<ITokenLexer<?>> lexers;

    public LinkedLexer(String descriptor) {
        lexers = new ArrayList<>();
        int n = descriptor.length();
        for (int i = 0; i < n; i++) {
            char ch = descriptor.charAt(i);
            ITokenLexer<?> item;
            switch (ch) {
            case 'N':
                item = NonspaceTokenLexer.INSTANCE;
                break;
            case 'S':
                item = QuotableTokenLexer.AS_IS;
                break;
            case '$':
                item = QuotableTokenLexer.DEQUOTED;
                break;
            case '*':
                item = CaptureTokenLexer.INSTANCE;
                break;
            case '@':
                item = new SpaceSeparatedLexer(QuotableTokenLexer.DEQUOTED);
                break;
            default:
                throw new IllegalArgumentException("Invalid descriptor char: '" + ch + "'");
            }
            lexers.add(item);
        }
    }

    @SafeVarargs
    public LinkedLexer(ITokenLexer<?>... lexers) {
        this.lexers = new ArrayList<>();
        for (ITokenLexer<?> lexer : lexers)
            this.lexers.add(lexer);
    }

    public LinkedLexer(List<ITokenLexer<?>> lexers) {
        if (lexers == null)
            throw new NullPointerException("lexers");
        this.lexers = lexers;
    }

    @Override
    public List<Object> lex(ILa1CharIn in) throws IOException, ParseException {
        List<Object> list = new ArrayList<>();
        int c;
        L: for (ITokenLexer<?> itemLexer : lexers) {
            for (;;) {
                if ((c = in.look()) == -1)
                    break L;
                if (Character.isWhitespace(c)) {
                    in.read();
                    continue;
                }
                Object item = itemLexer.lex(in);
                list.add(item);
                break;
            }
        }
        return list;
    }

    public static LinkedLexer EMPTY = new LinkedLexer();

}
