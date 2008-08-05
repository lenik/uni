package net.bodz.lapiota._extern;

import net.bodz.bas.cli.RunInfo;
import net.bodz.lapiota.wrappers.JavaLauncher;

@RunInfo(lib = { "ant.jar", "ant-launcher.jar" })
public class Ant extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.apache.tools.ant.launch.Launcher";
    }

    public static void main(String[] args) throws Throwable {
        new Ant().launch(args);
    }

}
