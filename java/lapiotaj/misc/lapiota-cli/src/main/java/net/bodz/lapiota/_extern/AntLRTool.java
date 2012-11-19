package net.bodz.lapiota._extern;

import net.bodz.bas.cli.boot.JavaLauncher;
import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.loader.boot.BootInfo;

@ProgramName("atool")
@BootInfo(syslibs = { "antlr" })
public class AntLRTool
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.antlr.Tool";
    }

    public static void main(String[] args)
            throws Exception {
        new AntLRTool().launch(args);
    }

}
