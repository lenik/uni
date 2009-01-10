package net.bodz.lapiota.javashell;

public abstract class _Command implements Command {

    protected final JavaShell shell;

    public _Command(JavaShell shell) {
        this.shell = shell;
    }

}
