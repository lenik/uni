package net.bodz.uni.catme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.catme.builtin.*;
import net.bodz.uni.catme.io.LoopRunner;
import net.bodz.uni.catme.io.ResourceResolver;
import net.bodz.uni.catme.io.ResourceVariant;
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

    boolean initScript() {
        scriptContext = PolyglotContext.createContext(scriptResolver);
        scriptContext.put(VAR_APP, this);

        Value bindings = scriptContext.getBindings();
        scriptContext.put(VAR_GLOBAL, bindings);

        try {
            scriptContext.include("./js/appInit.js");
            return true;
        } catch (Exception e) {
            logger.error("Failed to initialize app: " + e.getMessage(), e);
            return false;
        }
    }

    void runOnce() {
        if (!initScript())
            return;
        MainParser parser;
        try {
            parser = new MainParser(this, scriptContext);
            scriptContext.put(MainParser.VAR_PARSER, parser);
        } catch (Exception e) {
            logger.error("Failed to instantiate parser: " + e.getMessage(), e);
            return;
        }

        for (String arg : cmdlineArgs) {
            File file = new File(arg);
            if (file.exists()) {
                FileFrame frame = new FileFrame(parser, file);
                setupToplevel(frame);

                scriptContext.put(IFrame.VAR_FRAME, frame);

                try {
                    scriptContext.include("./js/fileArg.js");
                } catch (Exception e) {
                    logger.error("Failed to prepare file " + arg + ": " + e.getMessage(), e);
                    return;
                }

                try {
                    frame.parse();
                } catch (Exception e) {
                    logger.error("Failed to parse: " + e.getMessage(), e);
                }
                continue;
            }
            throw new IllegalArgumentException("invalid argument: " + arg);
        }
    }

    void setupToplevel(FileFrame frame) {
        frame.addCommand("include", InclusionCommand.INCLUDE);
        frame.addCommand("includeOnce", InclusionCommand.INCLUDE_ONCE);
        frame.addCommand("sinclude", InclusionCommand.SINCLUDE);
        frame.addCommand("mixin", InclusionCommand.MIXIN);
        frame.addCommand("import", InclusionCommand.IMPORT);

        frame.addCommand("dnl", DnlCommand.INSTANCE);
        frame.addCommand("stop", StopCommand.INSTANCE);

        frame.addCommand("echo", SetEchoLinesCommand.ECHO);
        frame.addCommand("noecho", SetEchoLinesCommand.NOECHO);
        frame.addCommand("skip", SetSkipLinesCommand.SKIP);
        frame.addCommand("noskip", SetSkipLinesCommand.NOSKIP);

        frame.addCommand("use", LoadJsCommand.INSTANCE);
        frame.addCommand("eval", EvalCommand.INSTANCE);
        frame.addCommand("shell", ShellCommand.INSTANCE);

        frame.addCommand("error", LogCommand.ERROR);
        frame.addCommand("warn", LogCommand.WARN);
        frame.addCommand("info", LogCommand.INFO);
        frame.addCommand("debug", LogCommand.DEBUG);
        frame.addCommand("trace", LogCommand.TRACE);
    }

    @Override
    protected void mainImpl(final String... args)
            throws Exception {
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
                    File file = new File(arg).getCanonicalFile();
                    File fileDir = file.getParentFile();
                    if (fileDir.isDirectory())
                        watchDirs.add(fileDir);
                }
            }
            new LoopRunner(() -> runOnce(), watchDirs).run();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new CatMe().execute(args);
    }

    static final Logger logger = LoggerFactory.getLogger(CatMe.class);

}
