package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.RunInfo;
import net.bodz.lapiota.loader.Lapiota;

@RunInfo(lib = "bodz_lapiota")
public class BatchProcessCLI extends net.bodz.bas.cli.BatchProcessCLI {

    static {
        Lapiota.load();
    }

}
