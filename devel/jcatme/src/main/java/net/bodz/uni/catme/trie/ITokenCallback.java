package net.bodz.uni.catme.trie;

import java.io.IOException;

import net.bodz.bas.err.ParseException;

public interface ITokenCallback<sym> {

    boolean onToken(TrieParser<sym> parser, Token<sym> token)
            throws IOException, ParseException;

}
