package net.bodz.lapiota._extern;

import net.bodz.bas.program.boot.JavaLauncher;
import net.bodz.bas.program.meta.ProgramName;

@ProgramName("atool")
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
