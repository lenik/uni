package net.bodz.uni.catme.lex;

import java.io.IOException;

public interface ILaCharIn
        extends
            ILa1CharIn {

    /**
     * Look len chars ahead.
     *
     * @return real number of chars filled. 0 for EOF.
     */
    int look(char[] buf, int off, int len)
            throws IOException;

}
