package net.bodz.uni.shelj;

import java.util.Locale;

import net.bodz.bas.i18n.nls.ClassResourceNlstr;
import net.bodz.bas.i18n.nls.INlsTranslator;

public class CliNlstr
        extends ClassResourceNlstr {

    public CliNlstr(INlsTranslator next, Locale locale) {
        super(next, locale);
    }

    public CliNlstr(INlsTranslator next) {
        super(next);
    }

    public static final CliNlstr INSTANCE = new CliNlstr(null);

}
