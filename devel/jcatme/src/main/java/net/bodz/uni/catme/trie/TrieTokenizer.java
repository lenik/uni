package net.bodz.uni.catme.trie;

import java.io.IOException;
import java.util.Iterator;

import net.bodz.bas.io.ICharIn;
import net.bodz.bas.t.iterator.PrefetchedIterator;
import net.bodz.bas.text.trie.CharTrie;
import net.bodz.bas.text.trie.CharTrie.Node;

/**
 * a pull-style parser.
 */
public class TrieTokenizer<sym>
        implements
            Iterable<Token<sym>> {

    CharTrie<sym> trie;
    Node<sym> root;
    Node<sym> cur;

    ICharIn in;
    int lineStart = 1, columnStart = 1;
    int line = lineStart, column = columnStart;
    int nextLine = line, nextColumn = column;

    boolean concat;
    TextBuf lookAhead = new TextBuf(16);
    TextBuf symbuf = new TextBuf(100);
    TextBuf cbuf = new TextBuf(4000);

    public TrieTokenizer(CharTrie<sym> trie, ICharIn in) {
        this.trie = trie;
        this.root = trie.getRoot();
        this.cur = root;
        this.in = in;
    }

    public CharTrie<sym> getTrie() {
        return trie;
    }

    public void setTrie(CharTrie<sym> trie) {
        this.trie = trie;
    }

    public void setStart(int lineStart, int columnStart) {
        this.line = this.lineStart = lineStart;
        this.column = this.columnStart = columnStart;
    }

    public void setLineStart(int lineStart) {
        this.line = this.lineStart = lineStart;
    }

    public void setColumnStart(int columnStart) {
        this.column = this.columnStart = columnStart;
    }

    public Token<sym> nextToken()
            throws IOException {
        char ch;
        for (;;) {
            if (!lookAhead.isEmpty()) {
                ch = lookAhead.shift();
                if (lookAhead.startLine != -1) {
                    nextLine = lookAhead.startLine;
                    nextColumn = lookAhead.startColumn;
                    lookAhead.startLine = -1;
                }
            } else {
                int c = in.read();
                if (c == -1)
                    break;
                ch = (char) c;
            }

            line = nextLine;
            column = nextColumn;

            switch (ch) {
            case '\r':
                nextColumn = columnStart;
                break;
            case '\n':
                nextLine = line + 1;
                nextColumn = columnStart;
                break;
            default:
                nextColumn = column + 1;
            }

            if (cur.isChild(ch)) {
                symbuf.append(ch);
                if (cbuf.hasContent()) {
                    symbuf.transferTo(lookAhead);
                    return cbuf.commit();
                }

                cur = cur.getChild(ch);
                if (cur.isDefined()) {
                    sym symbol = cur.getData();
                    cur = root;
                    return symbuf.commit(symbol);
                }
                continue;
            }

            cur = root;
            if (symbuf.hasContent())
                symbuf.transferTo(cbuf);
            cbuf.append(ch);
        }

        if (cbuf.hasContent())
            return cbuf.commit();
        return null;
    }

    class TextBuf {

        StringBuilder sb;
        int startLine;
        int startColumn;

        static final int _LEN_TEXTBUF = 16;

//        public TextBuf() {
//            sb = new StringBuilder(_LEN_TEXTBUF);
//        }

        public TextBuf(int capacity) {
            sb = new StringBuilder(capacity);
        }

        public boolean isEmpty() {
            return sb.length() == 0;
        }

        public boolean hasContent() {
            return sb.length() != 0;
        }

        public char shift() {
            assert sb.length() > 0;
            char head = sb.charAt(0);
            sb.deleteCharAt(0);
            return head;
        }

        public void append(char ch) {
            append(String.valueOf(ch));
        }

        public void append(String str) {
            if (sb.length() == 0) {
                startLine = line;
                startColumn = column;
            }
            sb.append(str);
        }

        public void transferTo(TextBuf other) {
            if (other.isEmpty()) {
                other.startLine = startLine;
                other.startColumn = startColumn;
            }
            other.sb.append(sb.toString());
            sb.setLength(0);
        }

        public Token<sym> commit() {
            return commit(null);
        }

        public Token<sym> commit(sym symbol) {
            Token<sym> token = new Token<sym>(startLine, startColumn, sb.toString(), symbol);
            sb.setLength(0);
            return token;
        }

        @Override
        public String toString() {
            return startLine + ":" + startColumn + ":" + sb;
        }

    }

    @Override
    public Iterator<Token<sym>> iterator() {
        return new Iter();
    }

    class Iter
            extends PrefetchedIterator<Token<sym>> {

        @Override
        protected Token<sym> fetch() {
            Token<sym> token;
            try {
                token = nextToken();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (token != null)
                return token;
            else
                return end();
        }

    }

}
