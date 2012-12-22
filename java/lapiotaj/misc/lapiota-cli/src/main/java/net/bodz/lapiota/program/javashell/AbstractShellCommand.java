package net.bodz.lapiota.program.javashell;

public abstract class AbstractShellCommand
        implements IShellCommand {

    protected final JavaShell shell;

    public AbstractShellCommand(JavaShell shell) {
        this.shell = shell;
    }

}
