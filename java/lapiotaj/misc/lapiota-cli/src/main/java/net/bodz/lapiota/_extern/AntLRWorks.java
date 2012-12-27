package net.bodz.lapiota._extern;

import net.bodz.bas.program.boot.JavaLauncher;
import net.bodz.bas.program.meta.ProgramName;

@ProgramName("aworks")
public class AntLRWorks
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.antlr.works.IDE";
    }

    public static void main(String[] args)
            throws Exception {
        new AntLRWorks().launch(args);
    }

}
