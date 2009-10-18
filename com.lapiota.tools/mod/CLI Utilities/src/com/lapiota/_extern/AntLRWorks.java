package com.lapiota._extern;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.cli.util.JavaLauncher;

@ProgramName("aworks")
@BootInfo(syslibs = { "antlrworks" })
public class AntLRWorks extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.antlr.works.IDE"; //$NON-NLS-1$
    }

    public static void main(String[] args) throws Exception {
        new AntLRWorks().launch(args);
    }

}
