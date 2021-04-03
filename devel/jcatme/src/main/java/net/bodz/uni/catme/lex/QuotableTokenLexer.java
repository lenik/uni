package net.bodz.uni.catme.lex;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.bodz.bas.err.ParseException;

public class QuotableTokenLexer
        implements
            ITokenLexer<String> {

    boolean dequoted;
    Set<Character> quoteChars = new HashSet<>();

    public QuotableTokenLexer(boolean dequoted) {
        this(dequoted, '"', '\'');
    }

    public QuotableTokenLexer(boolean dequoted, char... quoteChars) {
        this.dequoted = dequoted;
        for (char ch : quoteChars)
            this.quoteChars.add(ch);
    }

    static final int START = 0;
    static final int QUOTE = 1;
    static final int Q_ESC = 2;
    static final int Q_ESC_X = 3;
    static final int Q_ESC_U = 4;

    static final int _LEN_QUOTED = 1000;
    static final int _LEN_ESCSEQ = 8;

    @Override
    public String lex(ILa1CharIn in)
            throws IOException, ParseException {
        StringBuilder buf = new StringBuilder(_LEN_QUOTED);
        StringBuilder ebuf = new StringBuilder(_LEN_ESCSEQ);

        int state = START;
        int c;
        char lastQuoteChar = 0;

        L: while ((c = in.look()) != -1) {
            char ch = (char) c;

            S: switch (state) {
            case START:
                if (Character.isWhitespace(ch))
                    break L;
                if (quoteChars.contains(ch)) {
                    state = QUOTE;
                    lastQuoteChar = ch;
                    if (dequoted)
                        break;
                }
                buf.append(ch);
                break;

            case QUOTE:
                if (ch == lastQuoteChar) {
                    state = START;
                    if (dequoted)
                        break;
                } else if (ch == '\\') {
                    state = Q_ESC;
                    ebuf.append(ch);
                    break;
                }
                buf.append(ch);
                break;

            case Q_ESC:
                ebuf.append(ch);
                switch (ch) {
                case 'x':
                    state = Q_ESC_X;
                    break S;
                case 'u':
                    state = Q_ESC_U;
                    break S;
                default:
                    buf.append(decodeEscapeSeq(ebuf));
                    state = QUOTE;
                }
                break;

            case Q_ESC_X:
                if (isHex(ch)) {
                    ebuf.append(ch);
                    if (ebuf.length() < 4)
                        break;
                }
                buf.append(decodeEscapeSeq(ebuf));
                state = QUOTE;
                break;

            case Q_ESC_U:
                if (isHex(ch)) {
                    ebuf.append(ch);
                    if (ebuf.length() < 6)
                        break;
                }
                buf.append(decodeEscapeSeq(ebuf));
                state = QUOTE;
                break;
            } // SS
            in.read(); // consume
        }
        return buf.toString();
    }

    static boolean isHex(char ch) {
        if (ch >= '0' && ch <= '9')
            return true;
        if (ch >= 'A' && ch <= 'F')
            return true;
        if (ch >= 'a' && ch <= 'f')
            return true;
        return false;
    }

    protected String decodeEscapeSeq(StringBuilder ebuf)
            throws ParseException {
        String result;
        if (dequoted) {
            char ch = ebuf.charAt(1);
            switch (ch) {
            case 'n':
                ch = '\n';
                break;
            case 't':
                ch = '\t';
                break;
            case 'x':
                String xs = ebuf.substring(2);
                if (xs.isEmpty())
                    throw new ParseException("empty hex escape");
                int x = Integer.parseInt(xs, 16);
                ch = (char) x;
                break;
            case 'u':
                String us = ebuf.substring(2);
                if (us.length() != 4)
                    throw new ParseException("unicode escape should be 4 digits: " + us);
                int u = Integer.parseInt(us, 16);
                ch = (char) u;
                break;
            }
            result = String.valueOf(ch);
        } else {
            result = ebuf.toString();
        }
        ebuf.setLength(0);
        return result;
    }

    public static final QuotableTokenLexer AS_IS = new QuotableTokenLexer(false);
    public static final QuotableTokenLexer DEQUOTED = new QuotableTokenLexer(true);

}
