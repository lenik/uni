package net.bodz.bas.esm.base;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmName;

public class Esm_moment
        extends EsmModule {

    public Esm_moment(int priority) {
        super("moment", priority);
    }

    public final EsmName Moment = main.type("Moment");

}
