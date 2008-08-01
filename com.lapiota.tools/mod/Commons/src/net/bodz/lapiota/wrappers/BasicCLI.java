package net.bodz.lapiota.wrappers;

import net.bodz.lapiota.loader.Lapiota;

public class BasicCLI extends net.bodz.bas.cli.BasicCLI {

    static {
        Lapiota.load();
    }

}
