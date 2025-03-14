package net.bodz.bas.esm.lily;

import net.bodz.bas.esm.EsmModule;

public class LilyBasic
        extends EsmModule {

    public LilyBasic(int priority) {
        super("lily-basic", "src", priority);
    }

    static final String[] domainPatterns = { //
            "net.bodz.lily.concrete", //
            "net.bodz.lily.util", //
            "net.bodz.lily.schema", //
    };

    @Override
    public String[] getExclusiveDomains() {
        return domainPatterns;
    }

}
