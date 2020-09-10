package net.bodz.uni.catme;

import java.util.regex.Pattern;

import net.bodz.bas.c.java.io.ILineReader;
import net.bodz.bas.io.IPrintOut;

public abstract class BlockFilter
        implements IBlockFilter {

    @Override
    public void process(ILineReader in, IPrintOut out) {
    }

    public String theresOnlyInterestOnProcess;

}

class SubstBlock
        extends BlockFilter {

    Pattern pattern;
    String replacement;

}

class SubstBlock1
        extends BlockFilter {
}

class Indent
        extends SubstBlock {
}

class TabStop
        extends SubstBlock {
}

class Context
        extends BlockFilter {
}
