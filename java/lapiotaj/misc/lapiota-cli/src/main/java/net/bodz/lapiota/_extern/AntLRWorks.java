package net.bodz.lapiota._extern;

import net.bodz.bas.cli.util.JavaLauncher;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.program.ProgramName;

@ProgramName("aworks")
@BootInfo(syslibs = { "antlrworks" })
public class AntLRWorks
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.antlr.works.IDE"; //$NON-NLS-1$
    }

    public static void main(String[] args)
            throws Exception {
        new AntLRWorks().launch(args);
    }

}
