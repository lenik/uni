package net.bodz.uni.catme.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.java.util.regex.UnixStyleVarExpander;
import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.err.TransformException;
import net.bodz.bas.fn.ITransformer;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;
import net.bodz.uni.catme.ITextFilterClass;

public class VarInterpolatorClass
        implements
            ITextFilterClass,
            ITextFilter {

    @Override
    public boolean isScript() {
        return false;
    }

    @Override
    public ITextFilter createFilter(IFrame frame, List<String> args) {
        return this;
    }

    @Override
    public ITextFilterClass getFilterClass() {
        return VarInterpolatorClass.this;
    }

    public String transform2(IFrame frame, String input)
            throws TransformException {
        String defaultVal = null;
        boolean setDefault = false;
        int colonDash = input.indexOf(":-");
        if (colonDash != -1) {
            defaultVal = input.substring(colonDash + 2);
            input = input.substring(0, colonDash);
        } else {
            int eq = input.indexOf('=');
            if (eq != -1) {
                defaultVal = input.substring(eq + 1);
                input = input.substring(0, eq);
                setDefault = true;
            }
        }

        Object val;
        boolean localOnly = StringPred.isDecimal(input);
        if (localOnly) {
            Map<String, Object> localVarMap = frame.getLocalVarMap();
            val = localVarMap.get(input);
            if (val == null) {
                val = defaultVal;
                if (setDefault)
                    localVarMap.put(input, val);
            }
        } else {
            val = frame.getVar(input);
            if (val == null) {
                val = defaultVal;
                if (setDefault)
                    frame.putVar(input, val);
            }
        }

        String valStr = String.valueOf(val);
        return String.valueOf(valStr);
    }

    @Override
    public void filter(IFrame frame, StringBuilder in, Appendable out)
            throws IOException {
        UnixStyleVarExpander expander = new UnixStyleVarExpander(//
                new ITransformer<String, String>() {
                    @Override
                    public String transform(String input)
                            throws TransformException {
                        return transform2(frame, input);
                    }
                });
        String source = in.toString();
        String result = expander.process(source);
        out.append(result);
    }

}
