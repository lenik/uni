package net.bodz.uni.catme;

import java.io.File;

@Deprecated
public class TextBlock {

    TextBlock parent;

    File file;
    long start;
    long startLine;
    long end;
    long endLine;

    StringBuffer buffer = new StringBuffer();

    public TextBlock(TextBlock parent) {
        this.parent = parent;
    }

    public void append(String s) {
        buffer.append(s);
        if (parent != null)
            parent.append(s);
    }

    public String getText() {
        return buffer.toString();
    }

    @Override
    public String toString() {
        return getText();
    }

}
