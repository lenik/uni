package net.bodz.lapiota._extern;

import net.bodz.bas.cli.boot.JavaLauncher;

public class ASMifier
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.objectweb.asm.util.ASMifierClassVisitor";
    }

    public static void main(String[] args)
            throws Exception {
        new ASMifier().launch(args);
    }

}
