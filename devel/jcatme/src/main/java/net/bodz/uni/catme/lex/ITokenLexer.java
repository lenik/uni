package net.bodz.uni.catme.lex;

import java.io.IOException;

import net.bodz.bas.err.ParseException;

public interface ITokenLexer<T> {

    T lex(ILa1CharIn in)
            throws IOException, ParseException;

}
