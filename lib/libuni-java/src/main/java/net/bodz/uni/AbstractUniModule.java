package net.bodz.uni;

import net.bodz.bas.t.project.AbstractJazzModule;
import net.bodz.bas.t.project.IJazzProject;

public class AbstractUniModule
        extends AbstractJazzModule {

    @Override
    public IJazzProject getProject() {
        return UniProject.getInstance();
    }

}
