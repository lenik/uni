package net.bodz.uni.catme;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graalvm.polyglot.HostAccess.Export;

import net.bodz.bas.c.java.io.LineReader;
import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.c.system.SystemProperties;
import net.bodz.bas.io.res.AbstractStreamResource;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class MainParser {

    static final Logger logger = LoggerFactory.getLogger(MainParser.class);

    File pkgdatadir = new File("/usr/share/catme");
    File configDir;
    FileSearcher fileSearcher = new FileSearcher();
    List<File> libDirs;

    SrcLangType lang;
    String opener;
    String closer;
    String simpleOpener;
    String escapePrefix = "\"";

    /** main text stream (output) */
    public static final String TEXT = "text";
    Map<String, StringBuffer> streams = new HashMap<>();

    ScriptEngine scriptEngine;
    Invocable scriptInvocable;

    PrintStream out = System.out;

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

        scriptEngine = ScriptSupport.getInstance().getEngine();
        scriptInvocable = (Invocable) scriptEngine;

        scriptEngine.put("parser", this);

        String fileName = "js/main.js";
        String mainjs = loadTextResource(fileName);
        if (mainjs != null)
            try {
                scriptEngine.put(ScriptEngine.FILENAME, fileName);
                scriptEngine.eval(mainjs);
            } catch (Throwable e) {
                logger.error("Failed to load main.js: " + e.getMessage(), e);
            }
    }

    @Export
    public Object eval(String code)
            throws ScriptException {
        Object retval = scriptEngine.eval(code);
        return retval;
    }

    @Export
    public void parseFile(File file)
            throws IOException {
        logger.info("parseFile: " + file);
        FileFrame fileFrame = new FileFrame(file);
        String ext = fileFrame.getExtensionWithDot();

        String extension = fileFrame.getExtension();
        this.lang = SrcLangType.forExtension(extension);
        this.opener = lang.opener;
        this.closer = lang.closer;
        this.simpleOpener = lang.simpleOpener;

        // fileSearcher.reset();
        if (libDirs != null) {
            for (File dir : libDirs)
                fileSearcher.addSearchDir(dir);
        }
        String langLibName = lang.name() + "LIB";
        String langLibPath = System.getenv(langLibName.toUpperCase());
        if (langLibPath != null)
            fileSearcher.addPathEnv(langLibPath);

        File sysPathDir = new File(pkgdatadir, "path" + "/" + ext);
        fileSearcher.addPathDir(sysPathDir);
        File userPathDir = new File(configDir, "path" + "/" + ext);
        fileSearcher.addPathDir(userPathDir);

        FileResource resource = new FileResource(file, "utf-8");
        LineReader lineReader = resource.newLineReader();
        try {
            parse(fileFrame, lineReader);
        } finally {
            lineReader.close();
        }
    }

    void parse(Frame frame, LineReader lineReader)
            throws IOException {
        String line;
        // ParserState state = ParserState.NORMAL;
        StringBuilder commentLines = new StringBuilder();
        // StringBuilder commentBlock = new StringBuilder();

        while ((line = lineReader.readLine()) != null) {
            int pos;

            if (simpleOpener != null && (pos = line.indexOf(simpleOpener)) != -1) {
                // has simple comment
                String l = line.substring(0, pos);
                String comment = line.substring(pos + simpleOpener.length()).trim();

                if (!comment.startsWith(escapePrefix) && commentLines.length() == 0) {
                    parseText(frame, line);
                    continue;
                }

                parseLeftText(frame, l);

                if (comment.endsWith(" \\")) {
                    commentLines.append(comment.substring(0, comment.length() - 2));
                    continue;
                }

                if (commentLines.length() != 0) {
                    commentLines.append(comment);
                    comment = commentLines.toString();
                    commentLines.setLength(0);
                }

                parseInstruction(frame, comment);
                continue;
            }

            if (commentLines.length() != 0) {
                logger.error("Unnecessary \\ at the end of comment.");
                String comment = commentLines.toString();
                commentLines.setLength(0);
                parseInstruction(frame, comment);
                continue;
            }

            parseText(frame, line);
        } // while

    } // while

    @Export
    void parseLeftText(Frame frame, String text)
            throws IOException {
        parseText(frame, text);
    }

    @Export
    void parseRightText(Frame frame, String text)
            throws IOException {
        parseText(frame, text);
    }

    @Export
    void parseText(Frame frame, String text)
            throws IOException {
        scriptEngine.put("frame", frame);
        out.print(text);
    }

    @Export
    void parseInstruction(Frame frame, String instruction)
            throws IOException {
        String[] args = StringQuoted.split(instruction);
        List<String> list = new ArrayList<>();
        for (String arg : args)
            list.add(arg);

        try {
            scriptEngine.put("frame", frame);
            scriptInvocable.invokeFunction("parseInstruction", list);
        } catch (Exception e) {
            logger.error("Failed to parse at js side: " + e.getMessage(), e);
            // throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Export
    public SrcLangType getLang() {
        return lang;
    }

    @Export
    public String getOpener() {
        return opener;
    }

    public void setOpener(String opener) {
        this.opener = opener;
    }

    @Export
    public String getCloser() {
        return closer;
    }

    public void setCloser(String closer) {
        this.closer = closer;
    }

    @Export
    public String getSimpleOpener() {
        return simpleOpener;
    }

    public void setSimpleOpener(String simpleOpener) {
        this.simpleOpener = simpleOpener;
    }

    @Export
    public String getEscapePrefix() {
        return escapePrefix;
    }

    public void setEscapePrefix(String escapePrefix) {
        if (escapePrefix == null)
            throw new NullPointerException("escapePrefix");
        if (escapePrefix.isEmpty())
            throw new IllegalArgumentException("escapePrefix is empty.");
        this.escapePrefix = escapePrefix;
    }

    MavenPomDir pomDir = MavenPomDir.fromClass(getClass());

    @Export
    public String loadTextResource(String filename)
            throws IOException {
        AbstractStreamResource res = findResource(filename);
        if (res == null) {
            logger.error("Can't find resource " + filename);
            return null;
        }
        String text = res.read().readString();
        return text;
    }

    @Export
    public AbstractStreamResource findResource(String filename) {
        if (pomDir != null) {
            File resdir = pomDir.getResourceDir(getClass());
            File resfile = new File(resdir, filename);
            if (resfile.exists())
                return new FileResource(resfile);
        }

        URL url = getClass().getResource(filename);
        if (url != null)
            return new URLResource(url);

        for (File file : fileSearcher.search(filename))
            return new FileResource(file);
        return null;
    }

}
