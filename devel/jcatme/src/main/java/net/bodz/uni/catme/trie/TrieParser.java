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
    TextBuf otherbuf = new TextBuf();

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
                    if (otherbuf.hasContent())
                        if (!otherbuf.commit(callback))
                            return;
                    if (!symbuf.commit(callback, cur.getData()))
                        return;
                    cur = root;
                }
            } else {
                cur = root;
                if (symbuf.hasContent())
                    symbuf.transferTo(otherbuf);
                otherbuf.append(ch);
            }

            switch (ch) {
            case '\r':
                column = columnStart;
                break;
            case '\n':
                line = line + 1;
                column = columnStart;
                if (!otherbuf.commit(callback))
                    return;
                break;
            default:
                column = column + 1;
            }
        }

        if (otherbuf.hasContent())
            if (!otherbuf.commit(callback))
                return;
    }

    class TextBuf {

        private StringBuilder sb = new StringBuilder(16384);
        boolean hasContentAndMark;
        int startLine;
        int startColumn;

        public TextBuf() {
        }

        public boolean hasContent() {
            return hasContentAndMark;
        }

        public void append(char ch) {
            if (!hasContentAndMark) {
                startLine = line;
                startColumn = column;
                hasContentAndMark = true;
            }
            sb.append(ch);
        }

        public void append(String str) {
            if (!hasContentAndMark) {
                startLine = line;
                startColumn = column;
                hasContentAndMark = true;
            }
            sb.append(str);
        }

        public void transferTo(TextBuf other) {
            if (!hasContentAndMark)
                return;
            if (!other.hasContentAndMark) {
                other.startLine = startLine;
                other.startColumn = startColumn;
                other.hasContentAndMark = true;
            }
            other.sb.append(sb);
            sb.setLength(0);
            hasContentAndMark = false;
        }

        public boolean commit(ITokenCallback<sym> cb)
                throws IOException, ParseException {
            return commit(cb, null);
        }

        public boolean commit(ITokenCallback<sym> cb, sym symbol)
                throws IOException, ParseException {
            try {
                return cb.onToken(TrieParser.this, startLine, startColumn, sb, symbol);
            } finally {
                sb.setLength(0);
                hasContentAndMark = false;
            }
        }

        @Override
        public String toString() {
            if (hasContentAndMark)
                return startLine + ":" + startColumn + ":" + sb;
            else
                return "?:?:" + sb;
        }

    }

}
