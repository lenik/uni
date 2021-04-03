package net.bodz.uni.catme.js;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptException;

import net.bodz.uni.catme.ITextFilter;

public class InvokeFnFilter
        implements
            ITextFilter {

    Invocable invocable;
    String name;

    public InvokeFnFilter(Invocable invocable, String name) {
        this.invocable = invocable;
        this.name = name;
    }

    @Override
    public void filter(StringBuilder in, Appendable out)
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