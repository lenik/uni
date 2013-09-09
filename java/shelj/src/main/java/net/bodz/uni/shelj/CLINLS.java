package net.bodz.uni.shelj;

import java.util.Locale;

import net.bodz.bas.i18n.nls.ClassResourceNLS;
import net.bodz.bas.i18n.nls.NLS;

public class CLINLS
        extends ClassResourceNLS {

    public CLINLS(NLS next, Locale locale) {
        super(next, locale);
    }

    public CLINLS(NLS next) {
        super(next);
    }

    public static final CLINLS CLINLS = new CLINLS(null);

}
