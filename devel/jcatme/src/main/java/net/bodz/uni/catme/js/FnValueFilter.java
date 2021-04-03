package net.bodz.uni.catme.js;

import java.io.IOException;

import org.graalvm.polyglot.Value;

import net.bodz.uni.catme.ITextFilter;

public class FnValueFilter
        implements
            ITextFilter {

    Value fn;

    public FnValueFilter(Value fn) {
        this.fn = fn;
    }

    @Override
    public void filter(StringBuilder in, Appendable out)
            throws IOException {
        String s = in.toString();
        Value resultVal = fn.execute(s);
        Object obj = ValueFn.convert(resultVal);
        String resultStr = String.valueOf(obj);
        out.append(resultStr);
    }

}