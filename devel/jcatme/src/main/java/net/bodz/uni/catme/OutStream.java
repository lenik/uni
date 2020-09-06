package net.bodz.uni.catme;

import java.util.List;

public class OutStream {

    List<RenderBlock> blocks;
    boolean merged;

    boolean isCaching() {
        return !merged;
    }

    void writeBlock(RenderBlock block) {
        // if(merged)
        // out.write(block);
        blocks.add(block);
    }

}
