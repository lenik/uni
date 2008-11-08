package net.bodz.lapiota.ant.tasks;

import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

public class MkbatTask extends net.bodz.bas.cli.util.MkbatTask {

    static {
        Types.load(Lapiota.class);
    }

}
