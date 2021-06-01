package net.bodz.uni.shelj.c.builtin;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Help
        extends ShellCommand {

    public Help(JavaShell shell) {
        super(shell);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        String[] cmds = shell.commands.keySet().toArray(new String[0]);
        Arrays.sort(cmds);
        for (String cmd : cmds)
            shell.out.println(cmd);
    }

}
