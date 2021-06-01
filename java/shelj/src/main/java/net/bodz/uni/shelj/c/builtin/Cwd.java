package net.bodz.uni.shelj.c.builtin;

import net.bodz.bas.ctx.sys.UserDirVars;
import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Cwd
        extends ShellCommand {

    public Cwd(JavaShell shell) {
        super(shell);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        shell.out.println(UserDirVars.getInstance().get());
    }

}
