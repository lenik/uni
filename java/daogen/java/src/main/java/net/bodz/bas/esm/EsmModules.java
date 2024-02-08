package net.bodz.bas.esm;

import net.bodz.bas.esm.base.Esm_datatables;
import net.bodz.bas.esm.base.Esm_jQuery;
import net.bodz.bas.esm.base.Esm_moment;
import net.bodz.bas.esm.base.Esm_vue;
import net.bodz.bas.esm.skeljs.Core;
import net.bodz.bas.esm.skeljs.Dba;
import net.bodz.bas.esm.skeljs.LilyBasic;
import net.bodz.bas.esm.skeljs.LilyFab;
import net.bodz.bas.esm.skeljs.LilyViolet;

public interface EsmModules {

    int PRIORITY_BASE = 10;
    int PRIORITY_DATATABLES = 11;

    int PRIORITY_SKELJS = 20;
    int PRIORITY_LOCAL = 90;

    Esm_jQuery jQuery = new Esm_jQuery(PRIORITY_BASE);
    Esm_vue vue = new Esm_vue(PRIORITY_BASE);
    Esm_moment moment = new Esm_moment(PRIORITY_BASE);
    Esm_datatables datatables = new Esm_datatables(PRIORITY_DATATABLES);

    Core core = new Core(PRIORITY_SKELJS);
    Dba dba = new Dba(PRIORITY_SKELJS);

    LilyBasic basic = new LilyBasic(PRIORITY_SKELJS);
    LilyViolet violet = new LilyViolet(PRIORITY_SKELJS);
    LilyFab fab = new LilyFab(PRIORITY_SKELJS);

    EsmModule local = EsmModule.local(PRIORITY_LOCAL);

}
