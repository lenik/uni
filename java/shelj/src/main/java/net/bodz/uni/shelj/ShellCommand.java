package net.bodz.uni.shelj;

import net.bodz.bas.program.model.AbstractProgram;

public abstract class ShellCommand
        extends AbstractProgram {

    protected final JavaShell shell;

    public ShellCommand(JavaShell shell) {
        this.shell = shell;
    }

}
