package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

@RunInfo(lib = "bodz_lapiota")
public class BatchProcessCLI extends net.bodz.bas.cli.BatchProcessCLI {

    static {
        Types.load(Lapiota.class);
    }

}
