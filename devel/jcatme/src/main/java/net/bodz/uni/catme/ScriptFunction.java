package net.bodz.uni.catme;

import javax.script.Invocable;
import javax.script.ScriptException;

public class ScriptFunction
        implements
            IVarArgsFunction {

    Invocable invocable;
    String function;

    public ScriptFunction(Invocable invocable, String function) {
        if (invocable == null)
            throw new NullPointerException("invocable");
        if (function == null)
            throw new NullPointerException("function");
        this.invocable = invocable;
        this.function = function;
    }

    @Override
    public Object apply(Object... args)
            throws ScriptException {
        try {
            return invocable.invokeFunction(function, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
