package net.bodz.lapiota.eclipse.jdt;

import static java.lang.System.out;

public class ImpStatic {

    @SuppressWarnings("unchecked")
    public <T> T function() {
        EQ("H", (T) "Hello"); //$NON-NLS-1$ //$NON-NLS-2$
        out.println();
        return (T) null;
    }

}
