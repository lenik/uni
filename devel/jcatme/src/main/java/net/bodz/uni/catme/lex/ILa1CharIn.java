package net.bodz.uni.catme.lex;

import java.io.IOException;

import net.bodz.bas.io.ICharIn;

public interface ILa1CharIn
        extends
            ICharIn {

    int look()
            throws IOException;

}
