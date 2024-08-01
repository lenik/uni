package net.bodz.bas.esm.lily;

import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.IEsmModuleProvider;

public class LilyModules
        implements
            IEsmModuleProvider {

    public static final int PRIORITY_LILY_BASIC = 210;
    public static final int PRIORITY_LILY_VIOLET = 211;
    public static final int PRIORITY_LILY_FAB = 212;

    public static final LilyBasic basic = new LilyBasic(PRIORITY_LILY_BASIC);
    public static final LilyViolet violet = new LilyViolet(PRIORITY_LILY_VIOLET);
    public static final LilyFab fab = new LilyFab(PRIORITY_LILY_FAB);

    static final EsmModule[] MODULES = { basic, violet, fab };

    @Override
    public EsmModule[] getModules() {
        return MODULES;
    }

}
