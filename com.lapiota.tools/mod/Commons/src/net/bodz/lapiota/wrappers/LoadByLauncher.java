package net.bodz.lapiota.wrappers;

import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

public class LoadByLauncher extends net.bodz.bas.loader.LoadByLauncher {

    static {
        Types.load(Lapiota.class);
    }

    public static void main(String[] args) throws Throwable {
        net.bodz.bas.loader.LoadByLauncher.main(args);
    }

}
