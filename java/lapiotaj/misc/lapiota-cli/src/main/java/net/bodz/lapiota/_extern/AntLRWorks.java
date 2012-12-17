package net.bodz.lapiota._extern;

import net.bodz.bas.cli.boot.JavaLauncher;
import net.bodz.bas.cli.meta.ProgramName;

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
