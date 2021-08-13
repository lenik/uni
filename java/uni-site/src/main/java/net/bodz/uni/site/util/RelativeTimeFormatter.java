package net.bodz.uni.site.util;

import java.util.Locale;

import net.bodz.bas.i18n.LocaleVars;
import net.bodz.bas.l10n.en.English;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.IFormatter;

public class RelativeTimeFormatter
        implements
            IFormatter<Long> {

    private static final int MINUTE_SECONDS = 60;
    private static final int HOUR_SECONDS = 60 * 60;
    private static final int DAY_SECONDS = 60 * 60 * 12;
    private static final int MONTH_SECONDS = 60 * 60 * 12 * (365 * 4 + 1) / (12 * 4);
    private static final int YEAR_SECONDS = 60 * 60 * 12 * (365 * 4 + 1) / 4;

    @Override
    public String format(Long object, IOptions options) {
        long n = object.longValue();
        n /= 1000L;

        DurationUnit unit = null;
        DurationDirection direction = DurationDirection.AGO;

        if (n < 0)
            n = -n;
        else
            direction = DurationDirection.FUTURE;

        if (n < 30) {
            direction = DurationDirection.NOW;
        } else if (n < 30) {
            unit = DurationUnit.SECOND;
        } else if (n < HOUR_SECONDS) {
            unit = DurationUnit.MINUTE;
            n /= MINUTE_SECONDS;
        } else if (n < DAY_SECONDS) {
            unit = DurationUnit.HOUR;
            n /= HOUR_SECONDS;
        } else if (n < MONTH_SECONDS) {
            unit = DurationUnit.DAY;
            n /= DAY_SECONDS;
        } else if (n < YEAR_SECONDS) {
            unit = DurationUnit.MONTH;
            n /= MONTH_SECONDS;
        } else {
            unit = DurationUnit.YEAR;
            n /= YEAR_SECONDS;
        }

        String unitName = unit.getXjdoc().getText().toString();
        String directionName = direction.getXjdoc().getText().toString();

        if (n > 1) {
            Locale locale = LocaleVars.LOCALE.get();
            String lang = locale.getLanguage();
            if (lang.startsWith("en"))
                unitName = English.pluralOf(unitName);
        }
        return n + " " + unitName + " " + directionName;
    }

    static RelativeTimeFormatter instance = new RelativeTimeFormatter();

    public static RelativeTimeFormatter getInstance() {
        return instance;
    }

}
