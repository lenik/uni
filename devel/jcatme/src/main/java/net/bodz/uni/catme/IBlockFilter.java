package net.bodz.uni.catme;

import net.bodz.bas.c.java.io.ILineReader;
import net.bodz.bas.io.IPrintOut;

public interface IBlockFilter {

    void process(ILineReader in, IPrintOut out);
}
