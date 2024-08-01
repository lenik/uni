package net.bodz.bas.esm.lily;

import net.bodz.bas.esm.EsmModule;

public class LilyFab
        extends EsmModule {

    public LilyFab(int priority) {
        super("@lily/fab", "src", priority);
    }

    static final String[] packageNames = { //
            "net.bodz.violet.schema.fab", //
            "net.bodz.violet.schema.art.ArtifactModel", //
    };

    @Override
    public String[] getExclusivePackageNames() {
        return packageNames;
    }

}
