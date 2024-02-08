package net.bodz.bas.esm.base;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmName;

public class Esm_datatables
        extends EsmModule {

    public Esm_datatables(int priority) {
        super("datatables.net", priority);
    }

    public final EsmName Api = main.name("Api");

}
