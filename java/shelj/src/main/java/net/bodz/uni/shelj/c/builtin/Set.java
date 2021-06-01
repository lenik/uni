package net.bodz.uni.shelj.c.builtin;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.c.string.StringArray;
import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Set
        extends ShellCommand {

    public Set(JavaShell shell) {
        super(shell);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (args.length == 0)
            dumpEnv();
        else {
            String name = args[0];
            if (args.length == 1)
                dumpEnv(name);
            else
                args = Arrays.shift(args).array;
            shell.env.put(name, StringArray.join(" ", args));
        }
    }

    void dumpEnv() {
        for (String name : shell.env.keySet())
            dumpEnv(name);
    }

    void dumpEnv(String name) {
        Object value = shell.env.get(name);
        shell.out.println(name + " = " + value);
    }

}
