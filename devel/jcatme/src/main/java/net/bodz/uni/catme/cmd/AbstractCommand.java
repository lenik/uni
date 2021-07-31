package net.bodz.uni.catme.cmd;

import java.util.List;

import net.bodz.bas.text.qlex.CaptureTokenLexer;
import net.bodz.bas.text.qlex.ITokenLexer;
import net.bodz.bas.text.qlex.LinkedLexer;
import net.bodz.bas.text.qlex.QuotableTokenLexer;
import net.bodz.bas.text.qlex.SpaceSeparatedLexer;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.ICommand;
import net.bodz.uni.catme.IFrame;

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

    @Override
    public void prepare(IFrame frame, CommandOptions options, Object... args) {
    }

}
