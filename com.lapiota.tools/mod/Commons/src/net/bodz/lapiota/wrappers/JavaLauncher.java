package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

@RunInfo(lib = { "bodz_lapiota" })
public abstract class JavaLauncher extends net.bodz.bas.cli.util.JavaLauncher {

    static {
        Types.load(Lapiota.class);
    }

    public static void main(String[] args) throws Exception {
        net.bodz.bas.cli.util.JavaLauncher.main(args);
    }

}
