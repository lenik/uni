package net.bodz.uni.catme;

import javax.script.ScriptException;

public interface IVarArgsFunction {

    Object apply(Object... args)
            throws ScriptException;

}
