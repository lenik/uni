package net.bodz.uni.catme.js;

import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.ICommand;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.lex.ITokenLexer;
import net.bodz.uni.catme.lex.LinkedLexer;

public class FnValueCommand
        implements
            ICommand {

    String flags;
    Value fn;

    ITokenLexer<List<?>> argumentsLexer;

    public FnValueCommand(String flags, Value fn) {
        this.flags = flags;
        this.fn = fn;
        this.argumentsLexer = new LinkedLexer(flags);
    }

    @Override
    public boolean isScript() {
        return true;
    }

    @Override
    public CommandOptions parseOptions(String s) {
        return null;
    }

    @Override
    public ITokenLexer<List<?>> getArgumentsLexer() {
        return argumentsLexer;
    }

    @Override
    public void prepare(IFrame frame, CommandOptions options, Object... args) {
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args) {
        fn.execute(args);
    }

    @Override
    public String toString() {
        return "(cmd)/" + flags + ": " + fn;
    }

}
