package net.bodz.uni.catme;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.bodz.bas.c.system.SystemProperties;
import net.bodz.bas.io.res.builtin.FileResource;

public class MainParser {

    File pkgdatadir = new File("/usr/share/catme");
    File configDir;
    FileSearcher fileSearcher = new FileSearcher();
    List<File> libDirs;

    /** main text stream */
    public static final String TEXT = "text";
    Map<String, StringBuffer> streams = new HashMap<>();

    final ScriptEngine scriptEngine;
    final Invocable invocable;

    public MainParser()
            throws IOException {

        String cwd = SystemProperties.getUserDir();
        String home = SystemProperties.getUserHome();
        if (home == null)
            home = cwd;

        this.configDir = new File(home + "/.config/catme");

        String LIB = System.getenv("LIB");
        if (LIB != null)
            fileSearcher.addPathEnv(LIB);

        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("nashorn");
        invocable = (Invocable) scriptEngine;
    }

    public ScriptFunction getFunction(String function) {
        return new ScriptFunction(invocable, function);
    }

    public Object eval(String code)
            throws ScriptException {
        Object retval = scriptEngine.eval(code);
        return retval;
    }

    public void parse(File file)
            throws IOException {
        FileResource resource = new FileResource(file, "utf-8");
        FileFrame fileFrame = new FileFrame(file);
        String ext = fileFrame.getExtensionWithDot();

        // fileSearcher.reset();
        if (libDirs != null) {
            for (File dir : libDirs)
                fileSearcher.addSearchDir(dir);
        }
        String langLibName = fileFrame.lang.name() + "LIB";
        String langLibPath = System.getenv(langLibName.toUpperCase());
        if (langLibPath != null)
            fileSearcher.addPathEnv(langLibPath);

        File sysPathDir = new File(pkgdatadir, "path" + "/" + ext);
        fileSearcher.addPathDir(sysPathDir);
        File userPathDir = new File(configDir, "path" + "/" + ext);
        fileSearcher.addPathDir(userPathDir);

        Reader reader = resource.newReader();

    }

}
