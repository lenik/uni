package net.bodz.uni.catme.filter;

import java.io.IOException;

import net.bodz.bas.c.java.util.regex.UnixStyleVarExpander;
import net.bodz.bas.err.TransformException;
import net.bodz.bas.fn.ITransformer;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;

public class VarExpansionFilter
        implements
            ITextFilter,
            ITransformer<String, String> {

    IFrame frame;
    UnixStyleVarExpander expander = new UnixStyleVarExpander(this);

    public VarExpansionFilter(IFrame frame) {
        if (frame == null)
            throw new NullPointerException("frame");
        this.frame = frame;
    }

    @Override
    public String transform(String input)
            throws TransformException {
        Object value = frame.getVar(input);
        return String.valueOf(value);
    }

    @Override
    public void filter(StringBuilder in, Appendable out)
            throws IOException {
        String source = in.toString();
        String result = expander.process(source);
        out.append(result);
    }

}
