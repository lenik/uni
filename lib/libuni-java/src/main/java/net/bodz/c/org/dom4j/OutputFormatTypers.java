package net.bodz.c.org.dom4j;

import org.dom4j.io.OutputFormat;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.OutOfDomainException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractCommonTypers;
import net.bodz.bas.typer.std.IParser;

public class OutputFormatTypers
        extends AbstractCommonTypers<OutputFormat> {

    public OutputFormatTypers() {
        super(OutputFormat.class);
    }

    @Override
    protected Object queryInt(int typerIndex) {
        switch (typerIndex) {
        case IParser.typerIndex:
            return this;
        default:
            return null;
        }
    }

    @Override
    public OutputFormat parse(String fmt, IOptions options)
            throws ParseException {
        if ("normal".equals(fmt))
            return new OutputFormat();
        try {
            int indent = Integer.parseInt(fmt);
            String tab = Strings.repeat(indent, ' ');
            return new OutputFormat(tab);
        } catch (NumberFormatException e) {
        }
        if ("pretty".equals(fmt))
            return OutputFormat.createPrettyPrint();
        if ("compact".equals(fmt))
            return OutputFormat.createCompactFormat();
        throw new OutOfDomainException(fmt);
    }

}
