package net.bodz.lapiota;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.t.project.AbstractJazzProject;

@MainVersion({ 1, 0 })
public class KachoriLapiotaProject
        extends AbstractJazzProject {

    private static KachoriLapiotaProject instance = new KachoriLapiotaProject();

    public static KachoriLapiotaProject getInstance() {
        return instance;
    }

}
