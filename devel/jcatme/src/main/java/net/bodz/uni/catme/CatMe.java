package net.bodz.uni.catme;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.Value;

import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.catme.cmd.*;
import net.bodz.uni.catme.filter.VarInterpolatorClass;
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
public class CatMe
        extends BasicCLI {

    public static final String VAR_APP = CatMe.class.getSimpleName();
    public static final String VAR_GLOBAL = "global";

    /**
     * Specify text encoding/charset.
     *
     * <p lang="zh-cn">
     * 制定文字的编码/字符集。
     *
     * @option --encoding =CHARSET
     */
    Charset charset;

    /**
     * Edit files in place. (Only applied to the specified files on cmdline.)
     *
     * <p lang="zh-cn">
     * 将输出结果保存至原文件。（仅对命令行参数指定的文件）
     *
     * @option -i --in-place
     */
    boolean inPlace;

    /**
     * Keep a backup.
     *
     * @option --backup
     */
    boolean backup;

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

    /**
     * Operate in line-by-line mode.
     *
     * @option --break-lines
     */
    boolean breakLines = false;

    /**
     * Remove whitespace before single line instruction-comments.
     *
     * @option --remove-leads
     */
    boolean removeLeads = true;

    /**
     * @option --debug
     */
    boolean debug;

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

        for (int index = 0; index < cmdlineArgs.length; index++) {
            String arg = cmdlineArgs[index];
            if (index == 1)
                nonfirstStart();

            File file = new File(arg);
            if (!file.isFile()) {
                logger.error("Not a file: " + file);
                return;
            }

            FileFrame frame = new FileFrame(parser, file);
            if (charset != null)
                frame.charset = charset;
            frame.addFilter("vars", new VarInterpolatorClass());
            frame.beginFilter("vars");

            setupToplevel(frame);

            scriptContext.put(IFrame.VAR_FRAME, frame);

            try {
                scriptContext.include("./js/fileArg.js");
            } catch (Exception e) {
                logger.error("Failed to prepare file " + arg + ": " + e.getMessage(), e);
                return;
            }

            StringWriter outBuf = new StringWriter(4096);
            try {
                if (inPlace)
                    parser.out = outBuf;

                frame.parse();

                if (inPlace) {
                    if (backup) {
                        File bakFile = new File(file.getPath() + ".bak");
                        if (!file.renameTo(bakFile)) {
                            logger.error("Can't rename file for backup, aborted: " + file);
                            return;
                        }
                    }
                    String content = outBuf.toString();
                    logger.infof("Save in-place: %s (%d chars)", file, content.length());
                    new FileResource(file, frame.charset).write().writeString(content);
                }
            } catch (Exception e) {
                if (inPlace)
                    System.err.println(outBuf);
                logger.error("Failed to parse: " + e.getMessage(), e);
                return;
            }
            continue;
        }
    }

    public void nonfirstStart() {
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
