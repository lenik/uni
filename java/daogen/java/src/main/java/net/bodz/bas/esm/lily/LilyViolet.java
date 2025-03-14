package net.bodz.bas.esm.lily;

import net.bodz.bas.esm.EsmModule;

public class LilyViolet
        extends EsmModule {

    public LilyViolet(int priority) {
        super("lily-violet", "src", priority);
    }

    static final String[] domainPatterns = { //
            "net.bodz.violet.schema", //
    };

    @Override
    public String[] getExclusiveDomains() {
        return domainPatterns;
    }

}
