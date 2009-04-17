package net.bodz.lapiota._extern;

import net.bodz.bas.a.BootInfo;
import net.bodz.lapiota.wrappers.JavaLauncher;

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
