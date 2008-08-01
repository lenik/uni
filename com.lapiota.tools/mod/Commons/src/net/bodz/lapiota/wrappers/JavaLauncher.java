package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.RunInfo;
import net.bodz.lapiota.loader.Lapiota;

@RunInfo(lib = "bodz_bas")
public abstract class JavaLauncher extends net.bodz.bas.cli.util.JavaLauncher {

    static {
        Lapiota.load();
    }

}
