package net.bodz.lapiota._extern;

import net.bodz.bas.a.BootInfo;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.wrappers.JavaLauncher;

@BootInfo(syslibs = { "ant.jar", "ant-launcher.jar" })
public class Ant extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.apache.tools.ant.launch.Launcher"; //$NON-NLS-1$
    }

    public static void main(String[] args) throws Exception {
        new Ant().launch(args);
    }

}
