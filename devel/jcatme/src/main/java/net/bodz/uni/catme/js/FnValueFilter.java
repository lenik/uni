package net.bodz.uni.catme.js;

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
    public String filter(String s) {
        Value result = fn.execute(s);
        Object obj = ValueFn.convert(result);
        return String.valueOf(obj);
    }

}