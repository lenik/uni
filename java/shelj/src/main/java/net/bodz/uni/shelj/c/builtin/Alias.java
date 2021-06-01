package net.bodz.uni.shelj.c.builtin;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Alias
        extends ShellCommand {

    public Alias(JavaShell shell) {
        super(shell);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (args.length == 0) {
            dump();
            return;
        }
        for (String arg : args) {
            int eq = arg.indexOf('=');
            if (eq == -1) {
                dump(arg);
            } else {
                String alias = arg.substring(0, eq);
                String expand = arg.substring(eq + 1).trim();
                String[] tokens = StringQuoted.split(expand, ' ');
                shell.aliases.put(alias, tokens);
            }
        }
    }

    void dump() {
        for (String name : shell.aliases.keySet())
            dump(name);
    }

    void dump(String name) {
        String[] expansion = shell.aliases.get(name);
        String s = StringArray.join(" ", expansion); // quotes
        shell.out.println(nls.tr("alias ") + name + " = " + s);
    }

}
