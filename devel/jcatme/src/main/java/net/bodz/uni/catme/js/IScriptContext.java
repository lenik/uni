package net.bodz.uni.catme.js;

import java.io.IOException;

import javax.script.Invocable;

import net.bodz.bas.fn.EvalException;

public interface IScriptContext
        extends
            Invocable {

    Object get(String name);

    void put(String name, Object value);

    Object eval(String code)
            throws EvalException, IOException;

    Object eval(String code, String fileName)
            throws EvalException, IOException;

}
