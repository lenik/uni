package net.bodz.uni.catme.cmd;

import java.util.List;

import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.ICommand;
import net.bodz.uni.catme.lex.CaptureTokenLexer;
import net.bodz.uni.catme.lex.ITokenLexer;
import net.bodz.uni.catme.lex.LinkedLexer;
import net.bodz.uni.catme.lex.QuotableTokenLexer;
import net.bodz.uni.catme.lex.SpaceSeparatedLexer;

public abstract class AbstractCommand
        implements
            ICommand {

    protected static ITokenLexer<String> SCALAR = QuotableTokenLexer.DEQUOTED;
    protected static ITokenLexer<List<String>> LIST = SpaceSeparatedLexer.SS_DEQUOTED;
    protected static CaptureTokenLexer CAPTURE = CaptureTokenLexer.INSTANCE;

    ITokenLexer<List<?>> lexer;

    public AbstractCommand() {
        lexer = LinkedLexer.EMPTY;
    }

    public AbstractCommand(ITokenLexer<?>... lexers) {
        lexer = new LinkedLexer(lexers);
    }

    @Override
    public boolean isScript() {
        return false;
    }

    @Override
    public CommandOptions parseOptions(String s) {
        return null;
    }

    @Override
    public ITokenLexer<List<?>> getArgumentsLexer() {
        return lexer;
    }

}
