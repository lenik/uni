package net.bodz.lapiota.exttools;

import net.bodz.bas.cli.RunInfo;

@RunInfo(jar = { "ant", "ant-launcher" })
public class Ant {

    public static void main(String[] args) throws Throwable {
        ExtUtil.callMain("org.apache.tools.ant.launch.Launcher", args);
    }

}
