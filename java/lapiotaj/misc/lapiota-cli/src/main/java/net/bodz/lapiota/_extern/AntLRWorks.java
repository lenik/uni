package net.bodz.lapiota._extern;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.program.meta.ProgramName;
import net.bodz.bas.shell.Main;

@ProgramName("aworks")
public class AntLRWorks {

    static String FQCN = "org.antlr.works.IDE";

    public static void main(String[] args)
            throws Exception {
        String[] _args = Arrays.prepend(FQCN, args);
        Main.main(_args);
    }

}
