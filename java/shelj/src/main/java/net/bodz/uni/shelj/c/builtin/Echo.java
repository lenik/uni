package net.bodz.uni.shelj.c.builtin;

import net.bodz.bas.c.string.StringArray;
import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Echo
        extends ShellCommand {

    public Echo(JavaShell shell) {
        super(shell);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        String line = StringArray.join(" ", args);
        shell.out.println(line);
    }

}
