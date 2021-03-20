package net.bodz.uni.catme.js;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class ScriptEngineUtils {

    static final Logger logger = LoggerFactory.getLogger(ScriptEngineUtils.class);

    static boolean preCreateEngines = true;

    static ScriptEngineManager manager;
    static {
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

    // static String engineName = "Graal.js";
    static String engineName = "JavaScript";
    // static String engineName = "nashorn";

    public static ScriptEngine getEngine() {
        return manager.getEngineByName(engineName);
    }

}
