package net.bodz.lapiota._extern;

import net.bodz.bas.cli.util.JavaLauncher;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.program.ProgramName;

@ProgramName("atool")
@BootInfo(syslibs = { "antlr" })
public class AntLRTool
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.antlr.Tool"; //$NON-NLS-1$
    }

    public static void main(String[] args)
            throws Exception {
        new AntLRTool().launch(args);
    }

}
