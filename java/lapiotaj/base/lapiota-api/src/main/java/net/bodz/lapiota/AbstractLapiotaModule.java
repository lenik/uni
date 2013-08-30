package net.bodz.lapiota;

import net.bodz.bas.t.project.AbstractJazzModule;
import net.bodz.bas.t.project.IJazzProject;

public class AbstractLapiotaModule
        extends AbstractJazzModule {

    @Override
    public IJazzProject getProject() {
        return KachoriLapiotaProject.getInstance();
    }

}
