package net.bodz.uni.catme;

import java.util.Arrays;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSEngine {

    public static void main(String[] args)
            throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        engine.eval("");
        Invocable invocable = (Invocable) engine;
        List<Integer> list = Arrays.asList(1, 2, 3);
        ScriptContext context = engine.getContext();

    }

}
