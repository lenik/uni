package net.bodz.bas.t;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractCommonTypers;
import net.bodz.bas.typer.std.IFormatter;
import net.bodz.bas.typer.std.IParser;

public class Cc2Typer
        extends AbstractCommonTypers<Short> {

    public Cc2Typer() {
        super(Short.class);
    }

    @Override
    protected Object queryInt(int typerIndex) {
        switch (typerIndex) {
        case IFormatter.typerIndex:
        case IParser.typerIndex:
            return this;
        }
        return null;
    }

    @Override
    public String format(Short object, IOptions options) {
        int w = object & 0xffff;
        char a = (char) (w & 0xff);
        char b = (char) (w >> 8);
        return new String(new char[] { a, b });
    }

    @Override
    public Short parse(String text, IOptions options)
            throws ParseException {
        char a = text.charAt(0);
        char b = text.charAt(1);
        int w = b & 0xff;
        w <<= 8;
        w |= a & 0xff;
        return (short) w;
    }

}
