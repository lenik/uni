package net.bodz.uni.echo;

import net.bodz.bas.t.project.AbstractJazzModule;
import net.bodz.bas.t.project.IJazzProject;

public abstract class AbstractEchoModule
        extends AbstractJazzModule {

    @Override
    public IJazzProject getProject() {
        return UniEchoProject.getInstance();
    }

}
