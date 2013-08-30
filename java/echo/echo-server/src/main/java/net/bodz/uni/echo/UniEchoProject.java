package net.bodz.uni.echo;

import net.bodz.bas.t.project.AbstractJazzProject;

public class UniEchoProject
        extends AbstractJazzProject {

    private static UniEchoProject instance = new UniEchoProject();

    public static UniEchoProject getInstance() {
        return instance;
    }

}
