package net.bodz.lapiota._extern;

import net.bodz.bas.program.boot.JavaLauncher;

public class JFlex
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "JFlex.Main";
    }

    public static void main(String[] args)
            throws Exception {
        new JFlex().launch(args);
    }

}
