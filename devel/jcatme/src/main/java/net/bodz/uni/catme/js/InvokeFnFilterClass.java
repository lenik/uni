package net.bodz.uni.catme.js;

import java.io.IOException;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptException;

import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;
import net.bodz.uni.catme.ITextFilterClass;

public class InvokeFnFilterClass
        implements
            ITextFilterClass,
            ITextFilter {

    Invocable invocable;
    String name;

    public InvokeFnFilterClass(Invocable invocable, String name) {
        this.invocable = invocable;
        this.name = name;
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
        Object resultVal;
        try {
            resultVal = invocable.invokeFunction(name, s);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        String resultStr = String.valueOf(resultVal);
        out.append(resultStr);
    }

}