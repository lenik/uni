package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

@RunInfo(lib = "bodz_lapiota")
public class BatchCLI extends net.bodz.bas.cli.BatchCLI {

    static {
        Types.load(Lapiota.class);
    }

}
