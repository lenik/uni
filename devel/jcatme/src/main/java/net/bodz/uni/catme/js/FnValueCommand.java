package net.bodz.uni.catme.js;

import org.graalvm.polyglot.Value;

import net.bodz.uni.catme.ICommand;

public class FnValueCommand
        implements
            ICommand {

    public boolean greedy;
    public Value fn;

    @Override
    public boolean isGreedy() {
        return greedy;
    }

    @Override
    public void execute(Object... args) {
        fn.execute(args);
    }

}
