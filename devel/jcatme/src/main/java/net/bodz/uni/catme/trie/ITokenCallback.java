package net.bodz.uni.catme.trie;

import java.io.IOException;

import net.bodz.bas.err.ParseException;

public interface ITokenCallback<sym> {

    /**
     * @return <code>false</code> to quit the parse.
     */
    boolean onToken(TrieParser<sym> parser, int line, int column, StringBuilder cbuf, sym symbol)
            throws IOException, ParseException;

}
