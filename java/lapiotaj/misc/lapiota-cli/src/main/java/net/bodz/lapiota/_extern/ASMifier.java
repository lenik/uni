package net.bodz.lapiota._extern;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.shell.Main;

public class ASMifier {

    static String FQCN = "org.objectweb.asm.util.ASMifierClassVisitor";

    public static void main(String[] args)
            throws Exception {
        String[] _args = Arrays.prepend(FQCN, args);
        Main.main(_args);
    }

}
