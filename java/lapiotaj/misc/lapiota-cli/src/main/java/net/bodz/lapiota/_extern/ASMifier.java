package net.bodz.lapiota._extern;

import net.bodz.bas.cli.util.JavaLauncher;
import net.bodz.bas.loader.boot.BootInfo;

@BootInfo(syslibs = { "asm", "asm_util" })
public class ASMifier
        extends JavaLauncher {

    @Override
    protected String getMainClassName() {
        return "org.objectweb.asm.util.ASMifierClassVisitor"; //$NON-NLS-1$
    }

    public static void main(String[] args)
            throws Exception {
        new ASMifier().launch(args);
    }

}
