package net.bodz.lapiota._extern;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.shell.Main;

public class JFlex {

    static String FQCN = "JFlex.Main";

    public static void main(String[] args)
            throws Exception {
        String[] _args = Arrays.prepend(FQCN, args);
        Main.main(_args);
    }

}
