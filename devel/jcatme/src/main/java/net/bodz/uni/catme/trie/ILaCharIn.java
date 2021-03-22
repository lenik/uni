package net.bodz.uni.catme.trie;

import java.io.IOException;

import net.bodz.bas.io.ICharIn;

public interface ILaCharIn
        extends
            ICharIn {

    /**
     * Look len chars ahead.
     *
     * @return real number of chars filled. 0 for EOF.
     */
    int look(char[] buf, int off, int len)
            throws IOException;

}
