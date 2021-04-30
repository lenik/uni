package net.bodz.uni.catme.filter;

import java.io.IOException;
import java.util.List;

import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.uni.catme.FilterException;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;
import net.bodz.uni.catme.ITextFilterClass;

public class StringFilter
        implements
            ITextFilter {

    StringFilterClass filterClass;
    StringFnEnum fn;
    IFrame frame;
    List<String> args;

    public StringFilter(StringFilterClass filterClass, IFrame frame, List<String> args) {
        this.filterClass = filterClass;
        this.fn = filterClass.fn;
        this.frame = frame;
        this.args = args;
    }

    @Override
    public ITextFilterClass getFilterClass() {
        return filterClass;
    }

    @Override
    public void filter(IFrame frame, StringBuilder in, Appendable out)
            throws IOException, FilterException {
        switch (fn) {
        case NOP:
            out.append(in);
            break;

        case TO_UPPER:
            out.append(in.toString().toUpperCase());
            break;

        case TO_LOWER:
            out.append(in.toString().toUpperCase());
            break;

        case INDENT:
            if (args.isEmpty())
                throw new IllegalArgumentException("Expect indent size");

            String _size = args.get(1);
            if (!StringPred.isDecimal(_size))
                throw new IllegalArgumentException("Invalid indent size: " + _size);
            int size = Integer.parseInt(_size);
            indent(in, size, out);
            break;

        case TABULAR:
            throw new NotImplementedException();

        default:
            assert false;
        }
    }

    void indent(StringBuilder in, int size, Appendable out)
            throws IOException {
        String pads = Strings.repeat(size, " ");
        int start = 0;
        int pos;
        while ((pos = in.indexOf("\n", start)) != -1) {
            out.append(pads);
            out.append(in, start, pos);
            start = pos + 1;
        }
        out.append(pads);
        out.append(in, start, in.length());
    }

}
