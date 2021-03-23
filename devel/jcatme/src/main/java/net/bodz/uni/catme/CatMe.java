package net.bodz.uni.catme;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.catme.io.IWatcherCallback;
import net.bodz.uni.catme.io.ResourceResolver;
import net.bodz.uni.catme.io.ResourceVariant;
import net.bodz.uni.catme.io.WatcherWait;
import net.bodz.uni.catme.js.PolyglotContext;

/**
 * CatMe rewritten in Java.
 */
@MainVersion({ 0, 1 })
@ProgramName("jcatme")
@RcsKeywords(id = "$Id$")
public class CatMe extends BasicCLI {

    public static final String VAR_APP = CatMe.class.getSimpleName();
    public static final String VAR_GLOBAL = "global";

    /**
     * Monitor changes in the dirs and restart the app. This is useful when debugging.
     *
     * <p lang="zh-cn">
     * 监控指定的目录并自动重新运行，用于辅助调试目的。
     *
     * @option -W --watch-dir =PATH
     */
    List<File> watchDirs = new ArrayList<>();

    /**
     * Enable the watch mode. Auto restart the app when changes happen.
     *
     * <p lang="zh-cn">
     * 启用监控模式，当源文件发生变化时自动重新运行。
     *
     * @option -w --watch
     */
    boolean watchMode;

    ResourceResolver userResolver = new ResourceResolver();
    ResourceResolver scriptResolver = new ResourceResolver();

    PolyglotContext scriptContext;
    public Value resetFunction;
    public Value mainFunction;

    private String[] cmdlineArgs;

    public CatMe() {
        scriptResolver.searchClassResources = true;
        scriptResolver.searchPomDir = true;

        // userResolver.searchWorkDir = true;
        // userResolver.searchHomeDir = true;
        // userResolver.searchClassResources = true;
        userResolver.searchLibDirsForExtension = true;
        userResolver.searchEnvLangLIBs = true;
        userResolver.searchEnvLIB = true;
    }

    void runOnce1() {
        try {
            MainParser parser;
            try {
                parser = new MainParser(this, scriptContext);
            } catch (Exception e) {
                logger.error("Failed to instantiate parser: " + e.getMessage(), e);
                return;
            }

            for (String arg : cmdlineArgs) {
                File file = new File(arg);
                if (file.exists()) {
                    FileFrame frame = new FileFrame(parser, file);

                    scriptContext.put(MainParser.VAR_PARSER, parser);
                    scriptContext.put(IFrame.VAR_FRAME, frame);

                    frame.parse();
                    logger.info("parse end.");
                    continue;
                }
                throw new IllegalArgumentException("invalid argument: " + arg);
            }
        } catch (Exception e) {
            logger.error("Failed to parse: " + e.getMessage(), e);
        }
    }

    void runOnce() {
        for (String arg : cmdlineArgs)
            mainFunction.execute(arg);
    }

    @Override
    protected void mainImpl(final String... args)
            throws Exception {
        scriptContext = PolyglotContext.createContext(scriptResolver);
        scriptContext.put(VAR_APP, this);

        Value bindings = scriptContext.getBindings();
        scriptContext.put(VAR_GLOBAL, bindings);

        scriptContext.include("./js/test.mjs");
        logger.info("execute main function");
        resetFunction.execute(this);

        cmdlineArgs = args;
        runOnce();

        if (watchMode) {
            boolean watchScripts = true;
            boolean watchFiles = true;
            if (watchScripts) {
                ResourceVariant resource = scriptResolver.findResource("js/version.mjs");
                if (resource.type == ResourceVariant.FILE) {
                    File jsDir = resource.file.getParentFile();
                    watchDirs.add(jsDir);
                    File builtinsDir = new File(jsDir, "builtins");
                    watchDirs.add(builtinsDir);
                }
            }

            if (watchFiles) {
                for (String arg : args) {
                    File fileDir = new File(arg);
                    if (fileDir.isDirectory())
                        watchDirs.add(fileDir);
                }
            }

            WatcherWait watcherWait = new WatcherWait(new IWatcherCallback() {
                @Override
                public void onEvent(WatcherWait watcher, Path dir, WatchEvent<?> event) {
                    if (event == OVERFLOW)
                        return;

                    Path name = (Path) event.context();
                    Path path = dir.resolve(name);
                    logger.info("File changed: " + path + ", at " + new Date());

                    runOnce();

                    watcher.ignoreForAwhile();
                }
            });

            for (File dir : watchDirs)
                watcherWait.addFile(dir);

            watcherWait.run();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new CatMe().execute(args);
    }

    static final Logger logger = LoggerFactory.getLogger(CatMe.class);

}
