package net.bodz.uni.catme;

import java.util.List;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class ScriptSupport {

    static final Logger logger = LoggerFactory.getLogger(ScriptSupport.class);

    boolean preCreateEngines = true;

    ScriptEngineManager manager;
    ScriptEngine scriptEngine;

    public ScriptSupport() {
        // class-loader..
        manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        if (factories.isEmpty())
            throw new RuntimeException("No available engine.");

        logger.debug("Available script engines: ");
        for (ScriptEngineFactory fac : manager.getEngineFactories()) {
            logger.debug("  - " + fac.getEngineName());
            if (preCreateEngines) {
                try {
                    ScriptEngine se = fac.getScriptEngine();
                    logger.debug("    Pre-created: " + se);
                } catch (Throwable e) {
                    logger.error("Failed to get the engine: " + e.getMessage(), e);
                }
            }
        }
    }

    // String engineName = "Graal.js";
    String engineName = "JavaScript";
    // String engineName = "nashorn";

    public synchronized ScriptEngine getEngine() {
        if (scriptEngine == null) {
            scriptEngine = manager.getEngineByName(engineName);
            if (scriptEngine == null)
                throw new NullPointerException("Script engine isn't available: " + engineName);
        }
        return scriptEngine;
    }

    public ScriptContext getContext() {
        return getEngine().getContext();
    }

    public Invocable getInvocable() {
        return (Invocable) getEngine();
    }

    public Bindings getGlobalBindings() {
        return getEngine().getBindings(ScriptContext.GLOBAL_SCOPE);
    }

    public Bindings getBindings() {
        return getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
    }

    static ScriptSupport instance;

    public static ScriptSupport getInstance() {
        if (instance == null)
            synchronized (ScriptSupport.class) {
                if (instance == null)
                    instance = new ScriptSupport();
            }
        return instance;
    }

}
