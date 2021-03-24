package net.bodz.uni.catme.js;

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
    public String filter(String s) {
        Object result;
        try {
            result = invocable.invokeFunction(name, s);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ScriptException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return String.valueOf(result);
    }

}