package net.bodz.uni.catme.js;

import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.uni.catme.ICommand;
import net.bodz.uni.catme.lex.ITokenLexer;
import net.bodz.uni.catme.lex.MiniLexer;

public class FnValueCommand
        implements
            ICommand {

    String flags;
    Value fn;

    ITokenLexer<List<?>> argumentsLexer;

    public FnValueCommand(String flags, Value fn) {
        this.flags = flags;
        this.fn = fn;
        this.argumentsLexer = new MiniLexer(flags);
    }

    @Override
    public ITokenLexer<List<?>> getArgumentsLexer() {
        return argumentsLexer;
    }

    @Override
    public void execute(Object... args) {
        fn.execute(args);
    }

    @Override
    public String toString() {
        return "(cmd)/" + flags + ": " + fn;
    }

}
