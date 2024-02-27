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

    int PRIORITY_BASE = 100;
    int PRIORITY_DATATABLES = 101;

    int PRIORITY_SKELJS_CORE = 200;
    int PRIORITY_SKELJS_DBA = 201;

    int PRIORITY_LILY_BASIC = 210;
    int PRIORITY_LILY_VIOLET = 211;
    int PRIORITY_LILY_FAB = 212;

    int PRIORITY_LOCAL = 1000;

    Esm_jQuery jQuery = new Esm_jQuery(PRIORITY_BASE);
    Esm_vue vue = new Esm_vue(PRIORITY_BASE);
    Esm_moment momentTz = new Esm_moment(PRIORITY_BASE);
    Esm_datatables datatables = new Esm_datatables(PRIORITY_DATATABLES);

    Core core = new Core(PRIORITY_SKELJS_CORE);
    Dba dba = new Dba(PRIORITY_SKELJS_DBA);

    LilyBasic basic = new LilyBasic(PRIORITY_LILY_BASIC);
    LilyViolet violet = new LilyViolet(PRIORITY_LILY_VIOLET);
    LilyFab fab = new LilyFab(PRIORITY_LILY_FAB);

    EsmModule local = EsmModule.local(PRIORITY_LOCAL);

}
