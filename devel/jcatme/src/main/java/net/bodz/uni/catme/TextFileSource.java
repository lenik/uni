package net.bodz.uni.catme;

import java.io.File;
import java.util.Stack;

@Deprecated
public class TextFileSource {

    File file;
    long position;
    long line;

    Stack<TextBlock> blocks = new Stack<>();

    public synchronized TextBlock beginBlock() {
        TextBlock parent = null;
        if (blocks.isEmpty())
            parent = blocks.peek();
        TextBlock block = new TextBlock(parent);
        block.file = file;
        block.start = position;
        block.startLine = line;
        return block;
    }

    public synchronized TextBlock endBlock() {
        if (blocks.isEmpty())
            throw new IllegalStateException("not in a block");

        TextBlock block = blocks.pop();
        block.end = position;
        block.endLine = line;
        return block;
    }

}
