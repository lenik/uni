package net.bodz.uni.catme;

import java.io.File;

public class TextFileSource {

    File file;
    long position;
    long blockStart;
    StringBuilder readBuffer;

    public synchronized RenderBlock blockFinish() {
        FileDataSelection selection = new FileDataSelection();
        selection.file = file;
        selection.start = blockStart;
        selection.end = position;
        blockStart = position;
        RenderBlock block = new RenderBlock();
        block.content = readBuffer.toString();
        readBuffer.setLength(0);
        block.source = selection;
        return block;
    }

}
