package net.bodz.uni.catme;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import javax.script.Bindings;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * CatMe rewritten in Java.
 */
@MainVersion({ 0, 1 })
@ProgramName("jcatme")
@RcsKeywords(id = "$Id$")
public class CatMe
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(CatMe.class);

    String[] args;

    public CatMe() {
        Bindings globalScope = ScriptSupport.getInstance().getGlobalBindings();
        globalScope.put("app", this);
    }

    void runOnce() {
        try {
            logger.info("args: " + Arrays.asList(args));
            MainParser parser;
            try {
                parser = new MainParser();
            } catch (Exception e) {
                logger.error("Failed to instantiate parser: " + e.getMessage(), e);
                return;
            }
            logger.info("parser: " + parser);
            for (String arg : args) {
                File file = new File(arg);
                if (file.exists()) {
                    parser.parseFile(file);
                    continue;
                }
                throw new IllegalArgumentException("invalid argument: " + arg);
            }
        } catch (Exception e) {
            logger.error("Failed to parse: " + e.getMessage(), e);
        }
    }

    @Override
    protected void mainImpl(final String... args)
            throws Exception {
        this.args = args;

        WatcherWait watcherWait = new WatcherWait(new IWatcherCallback() {
            @Override
            public void onCreate(Path file) {
                String name = file.getFileName().toString();
                if ("main.js".equals(name)) {
                    System.out.println();
                    System.out.println("main.js changed!" + System.currentTimeMillis());
                    // Thread.sleep(10);
                    runOnce();
                }
            }
        });
        MavenPomDir pomDir = MavenPomDir.fromClass(getClass());
        File resdir = pomDir.getResourceDir(getClass());
        File mainjs = new File(resdir, "js/main.js");
        watcherWait.addFile(mainjs.getParentFile());

        runOnce();
        watcherWait.run();
    }

    public static void main(String[] args)
            throws Exception {
        new CatMe().execute(args);
    }

}
