package net.bodz.uni.catme.lex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.err.ParseException;

public class MiniLexer
        implements
            ITokenLexer<List<?>> {

    List<ITokenLexer<?>> lexers;

    public MiniLexer(String flags) {
        lexers = new ArrayList<>();
        int n = flags.length();
        for (int i = 0; i < n; i++) {
            char ch = flags.charAt(i);
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
                item = new WordListLexer(QuotableTokenLexer.DEQUOTED);
                break;
            default:
                throw new IllegalArgumentException("Invalid flag: '" + ch + "'");
            }
            lexers.add(item);
        }
    }

    @SafeVarargs
    public MiniLexer(ITokenLexer<String>... lexers) {
        this.lexers = new ArrayList<>();
        for (ITokenLexer<String> lexer : lexers)
            this.lexers.add(lexer);
    }

    public MiniLexer(List<ITokenLexer<?>> lexers) {
        if (lexers == null)
            throw new NullPointerException("lexers");
        this.lexers = lexers;
    }

    @Override
    public List<Object> lex(ILa1CharIn in)
            throws IOException, ParseException {
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

}
