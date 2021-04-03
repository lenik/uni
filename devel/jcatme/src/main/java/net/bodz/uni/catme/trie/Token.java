package net.bodz.uni.catme.trie;

public class Token<sym> {

    public int line;
    public int column;

    public String text;
    public sym symbol;

    public Token(int line, int column, String text, sym symbol) {
        if (text == null)
            throw new NullPointerException("text");
        this.line = line;
        this.column = column;
        this.text = text;
        this.symbol = symbol;
    }

    public Token(int line, int column, char ch) {
        this.line = line;
        this.column = column;
        this.text = String.valueOf(ch);
    }

    public Token(int line, int column, String text) {
        if (text == null)
            throw new NullPointerException("text");
        this.line = line;
        this.column = column;
        this.text = text;
    }

    public boolean isSymbol() {
        return symbol != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(text.length() + 16);
        sb.append(line);
        sb.append(":");
        sb.append(column);
        sb.append(":");
        sb.append(text);
        return sb.toString();
    }

}
