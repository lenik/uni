package com.lapiota._extern;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.cli.util.JavaLauncher;

@BootInfo(syslibs = { "ant", "ant-launcher.jar", "tools.jar", "dt.jar" })
public class Ant extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.apache.tools.ant.launch.Launcher"; //$NON-NLS-1$
    }

    public static void main(String[] args) throws Exception {
        new Ant().launch(args);
    }

}
