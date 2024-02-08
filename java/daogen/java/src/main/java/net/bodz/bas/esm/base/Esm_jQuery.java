package net.bodz.bas.esm.base;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmName;

public class Esm_jQuery
        extends EsmModule {

    public Esm_jQuery(int priority) {
        super("jquery", priority);
    }

    public final EsmName $ = main.defaultExport("$");

}
