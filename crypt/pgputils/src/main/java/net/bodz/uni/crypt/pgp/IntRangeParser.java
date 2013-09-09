package net.bodz.uni.crypt.pgp;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractParser;

/**
 * @see net.bodz.bas.t.set.IntRange
 */
@Deprecated
public class IntRangeParser
        extends AbstractParser<IntRange> {

    @Override
    public IntRange parse(String text, IOptions options)
            throws ParseException {
        int comma = text.indexOf(',');
        int from = Integer.parseInt(text.substring(0, comma));
        int to = Integer.parseInt(text.substring(comma + 1));
        return new IntRange(from, to);
    }

}
