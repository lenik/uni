package net.bodz.uni.catme;

import java.io.IOException;

public interface ITextFilter {

    ITextFilterClass getFilterClass();

    void filter(IFrame frame, StringBuilder in, Appendable out)
            throws IOException, FilterException;

}
