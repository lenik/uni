package net.bodz.uni;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.t.project.AbstractJazzProject;

@MainVersion({ 1, 1 })
public class UniProject
        extends AbstractJazzProject {

    private static UniProject instance = new UniProject();

    public static UniProject getInstance() {
        return instance;
    }

}
