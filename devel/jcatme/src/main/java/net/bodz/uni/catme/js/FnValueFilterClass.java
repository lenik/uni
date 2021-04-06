package net.bodz.uni.catme.js;

import java.io.IOException;
import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;
import net.bodz.uni.catme.ITextFilterClass;

public class FnValueFilterClass
        implements
            ITextFilterClass,
            ITextFilter {

    Value fn;

    public FnValueFilterClass(Value fn) {
        this.fn = fn;
    }

    @Override
    public boolean isScript() {
        return true;
    }

    @Override
    public ITextFilter createFilter(IFrame frame, List<String> args) {
        return this;
    }

    @Override
    public ITextFilterClass getFilterClass() {
        return this;
    }

    @Override
    public void filter(IFrame frame, StringBuilder in, Appendable out)
            throws IOException {
        String s = in.toString();
        Value resultVal = fn.execute(s);
        Object obj = ValueFn.convert(resultVal);
        String resultStr = String.valueOf(obj);
        out.append(resultStr);
    }

}