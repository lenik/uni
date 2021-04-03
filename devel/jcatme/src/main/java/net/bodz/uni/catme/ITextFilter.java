package net.bodz.uni.catme;

import java.io.IOException;

public interface ITextFilter {

    void filter(StringBuilder in, Appendable out)
            throws IOException;

}
