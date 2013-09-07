package net.bodz.uni.pgputils;

import net.bodz.bas.i18n.nls.II18nCapable;

/**
 * @see net.bodz.bas.t.set.IntRange
 */
@Deprecated
public class IntRange
        implements II18nCapable {

    public int from;
    public int to;

    public IntRange(int from, int to) {
        if (from >= to)
            throw new IllegalArgumentException(//
                    tr._("from ") + from + " > to " + to);
        this.from = from;
        this.to = to;
    }

}
