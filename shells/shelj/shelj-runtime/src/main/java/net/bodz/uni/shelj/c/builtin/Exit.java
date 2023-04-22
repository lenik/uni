package net.bodz.uni.shelj.c.builtin;

import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Exit
        extends ShellCommand {

    public Exit(JavaShell shell) {
        super(shell);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        int status = 0;
        if (args.length > 0) {
            status = Integer.parseInt(args[0]);
        }
        shell.requestExit(status);
    }

}
