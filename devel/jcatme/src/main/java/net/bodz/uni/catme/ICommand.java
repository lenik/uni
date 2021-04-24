package net.bodz.uni.catme;

import java.io.IOException;
import java.util.List;

import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.lex.ITokenLexer;

public interface ICommand {

    boolean isScript();

    ITokenLexer<List<?>> getArgumentsLexer();

    CommandOptions parseOptions(String s);

    void prepare(IFrame frame, CommandOptions options, Object... args);

    void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException;

}
