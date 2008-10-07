package net.bodz.lapiota._extern;

import net.bodz.bas.cli.a.RunInfo;
import net.bodz.lapiota.wrappers.JavaLauncher;

@RunInfo(lib = "jflex")
public class JFlex extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "JFlex.Main";
    }

    public static void main(String[] args) throws Exception {
        new JFlex().launch(args);
    }

}
