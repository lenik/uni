package net.bodz.lapiota.wrappers;

import net.bodz.lapiota.util.Lapiota;

public class BatchProcessCLI extends net.bodz.bas.cli.BatchProcessCLI {

    static {
        Lapiota.load();
    }

}
