package net.bodz.uni.catme.js;

import java.io.IOException;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.fn.EvalException;

public class ScriptEngineContext
        implements
            IScriptContext {

    ScriptEngine scriptEngine;
    Invocable invocable;
    Bindings bindings;

    public ScriptEngineContext(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        this.invocable = (Invocable) scriptEngine;
        this.bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public Object get(String name) {
        return scriptEngine.get(name);
    }

    @Override
    public void put(String name, Object value) {
        scriptEngine.put(name, value);
    }

    @Override
    public Object eval(String code, String fileName)
            throws EvalException, IOException {
        put(ScriptEngine.FILENAME, fileName);
        return eval(code);
    }

    @Override
    public Object eval(String code)
            throws EvalException {
        try {
            return scriptEngine.eval(code);
        } catch (ScriptException e) {
            throw new EvalException(e.getMessage(), e);
        }
    }

    @Override
    public Object include(String filename)
            throws EvalException, IOException {
        throw new NotImplementedException();
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args)
            throws ScriptException, NoSuchMethodException {
        return invocable.invokeMethod(thiz, name, args);
    }

    @Override
    public Object invokeFunction(String name, Object... args)
            throws ScriptException, NoSuchMethodException {
        return invocable.invokeFunction(name, args);
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return invocable.getInterface(clasz);
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return invocable.getInterface(thiz, clasz);
    }

}
