package net.bodz.lapiota.wrappers;

import net.bodz.bas.a.BootInfo;

@BootInfo(userlibs = "bodz_lapiota")
public abstract class JavaLauncher extends net.bodz.bas.cli.util.JavaLauncher {

    public static void main(String[] args) throws Exception {
        net.bodz.bas.cli.util.JavaLauncher.main(args);
    }

}
