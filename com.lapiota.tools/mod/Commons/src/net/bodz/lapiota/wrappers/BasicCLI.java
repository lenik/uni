package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

@RunInfo(lib = "bodz_lapiota")
public class BasicCLI extends net.bodz.bas.cli.BasicCLI {

    static {
        Types.load(Lapiota.class);
    }

}
