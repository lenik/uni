package net.bodz.bas.t;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractCommonTypers;
import net.bodz.bas.typer.std.IFormatter;
import net.bodz.bas.typer.std.IParser;

public class Cc4Typer
        extends AbstractCommonTypers<Integer> {

    public Cc4Typer() {
        super(Integer.class);
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
    public String format(Integer object, IOptions options) {
        int dw = object;

        char a = (char) (dw & 0xff);
        dw >>= 8;
        char b = (char) (dw & 0xff);
        dw >>= 8;
        char c = (char) (dw & 0xff);
        dw >>= 8;
        char d = (char) (dw & 0xff);

        return new String(new char[] { a, b, c, d });
    }

    @Override
    public Integer parse(String text, IOptions options)
            throws ParseException {
        char a = text.charAt(0);
        char b = text.charAt(1);
        char c = text.charAt(2);
        char d = text.charAt(3);
        int dw = d & 0xff;
        dw <<= 8;
        dw |= c & 0xff;
        dw <<= 8;
        dw |= b & 0xff;
        dw <<= 8;
        dw |= a & 0xff;
        return dw;
    }

}
