package com.lapiota._extern;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.cli.util.JavaLauncher;

@BootInfo(syslibs = "jflex")
public class JFlex extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "JFlex.Main"; //$NON-NLS-1$
    }

    public static void main(String[] args) throws Exception {
        new JFlex().launch(args);
    }

}
