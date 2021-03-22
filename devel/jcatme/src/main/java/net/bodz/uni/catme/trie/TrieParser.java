package net.bodz.uni.catme.trie;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.ICharIn;
import net.bodz.bas.text.trie.CharTrie;
import net.bodz.bas.text.trie.CharTrie.Node;

public class TrieParser<sym> {

    CharTrie<sym> trie;
    Node<sym> root;
    private Node<sym> cur;

    ICharIn in;
    int lineStart = 1, columnStart = 1;
    int line = lineStart, column = columnStart;

    TextBuf symbuf = new TextBuf();
    TextBuf cbuf = new TextBuf();

    ITokenCallback<sym> callback;

    public TrieParser(CharTrie<sym> trie, ICharIn in, ITokenCallback<sym> callback) {
        this.trie = trie;
        this.root = trie.getRoot();
        this.cur = root;
        this.in = in;
        this.callback = callback;
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

    public void parse()
            throws IOException, ParseException {
        char ch;
        for (;;) {
            int c = in.read();
            if (c == -1)
                break;
            ch = (char) c;

            if (cur.isChild(ch)) {
                symbuf.append(ch);
                cur = cur.getChild(ch);
                if (cur.isDefined()) { // TODO lookahead.
                    if (cbuf.hasContent())
                        if (!callback.onToken(this, cbuf.commit()))
                            return;
                    if (!callback.onToken(this, symbuf.commit(cur.getData())))
                        return;
                    cur = root;
                }
            } else {
                cur = root;
                if (symbuf.hasContent())
                    symbuf.transferTo(cbuf);
                cbuf.append(ch);
            }

            switch (ch) {
            case '\r':
                column = columnStart;
                break;
            case '\n':
                line = line + 1;
                column = columnStart;
                if (!callback.onToken(this, cbuf.commit()))
                    return;
                break;
            default:
                column = column + 1;
            }
        }

        if (cbuf.hasContent())
            if (!callback.onToken(this, cbuf.commit()))
                return;
    }

    class TextBuf {

        StringBuilder sb = new StringBuilder();
        int startLine;
        int startColumn;

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

}
