package net.bodz.uni.catme;

import java.util.List;

import net.bodz.uni.catme.lex.ITokenLexer;

public interface ICommand {

    ITokenLexer<List<?>> getArgumentsLexer();

    void execute(Object... args);

}
