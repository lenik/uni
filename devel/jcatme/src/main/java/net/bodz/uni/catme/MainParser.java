package net.bodz.uni.catme;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;

import net.bodz.bas.c.java.io.LineReader;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.fn.EvalException;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.io.ResourceResolver;
import net.bodz.uni.catme.io.ResourceVariant;
import net.bodz.uni.catme.js.IScriptContext;
import net.bodz.uni.catme.js.PolyglotContext;

public class MainParser {

    static final Logger logger = LoggerFactory.getLogger(MainParser.class);

    public static final String VAR_APP = CatMe.class.getSimpleName();
    public static final String VAR_THIS = MainParser.class.getSimpleName();
    public static final String VAR_FRAME = Frame.class.getSimpleName();

    CatMe app;

    SrcLangType lang;
    String extension;
    String opener;
    String closer;
    String simpleOpener;
    String escapePrefix = "\\";

    /** main text stream (output) */
    public static final String TEXT = "text";
    Map<String, StringBuffer> streams = new HashMap<>();

    public ResourceResolver resourceResolver = new ResourceResolver();
    IScriptContext scriptContext;

    PrintStream out = System.out;

    public MainParser(CatMe app)
            throws IOException {
        this.app = app;
    }

    public Object eval(String code)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        return scriptContext.eval(code);
    }

    public Object eval(String code, String fileName)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        return scriptContext.eval(code, fileName);
    }

    public PrintStream getOut() {
        return out;
    }

    public void parseFile(File file)
            throws IOException, EvalException {
        logger.info("parseFile: " + file);
        FileFrame fileFrame = new FileFrame(file);
        // String dotExt = fileFrame.getExtensionWithDot();

        this.extension = fileFrame.getExtension();
        this.lang = SrcLangType.forExtension(extension);
        this.opener = lang.opener;
        this.closer = lang.closer;
        this.simpleOpener = lang.simpleOpener;

        scriptContext = PolyglotContext.js(resourceResolver);
        scriptContext.put(VAR_APP, app);
        scriptContext.put(VAR_THIS, this);
        scriptContext.put(VAR_FRAME, fileFrame);

        scriptContext.eval("load('./js/main.mjs')");

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

    void parseLeftText(Frame frame, String text)
            throws IOException {
        parseText(frame, text);
    }

    void parseRightText(Frame frame, String text)
            throws IOException {
        parseText(frame, text);
    }

    void parseText(Frame frame, String text)
            throws IOException {
        scriptContext.put(VAR_FRAME, frame);
        out.print(text);
    }

    Value cmdlineParser;

    public Value getCmdlineParser() {
        return cmdlineParser;
    }

    public void setCmdlineParser(Value cmdlineParser) {
        this.cmdlineParser = cmdlineParser;
    }

    void parseInstruction(Frame frame, String instruction)
            throws IOException {
        if (cmdlineParser == null)
            throw new IllegalStateException("cmdlineParser wasn't set.");

        String[] args = StringQuoted.split(instruction);
        List<String> list = new ArrayList<>();
        for (String arg : args)
            list.add(arg);

        try {
            scriptContext.put(VAR_FRAME, frame);
            cmdlineParser.invokeMember("apply", null, list);
        } catch (Exception e) {
            logger.error("Failed to parse at js side: " + e.getMessage(), e);
            // throw new RuntimeException(e.getMessage(), e);
        }
    }

    public SrcLangType getLang() {
        return lang;
    }

    public String getOpener() {
        return opener;
    }

    public void setOpener(String opener) {
        this.opener = opener;
    }

    public String getCloser() {
        return closer;
    }

    public void setCloser(String closer) {
        this.closer = closer;
    }

    public String getSimpleOpener() {
        return simpleOpener;
    }

    public void setSimpleOpener(String simpleOpener) {
        this.simpleOpener = simpleOpener;
    }

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

    public Set<String> imported = new HashSet<>();

    public String resolveQName(String fqn)
            throws IOException {
        String fileName = fqn.replace('.', '/') + "." + extension;
        return fileName;
    }

    public void parseChild(Frame parent, String href)
            throws IOException {
        ResourceVariant resource = resourceResolver.findResource(href);
        if (resource == null)
            throw new IOException("Can't find file " + href + ".");
        if (resource.type != ResourceVariant.FILE)
            throw new NotImplementedException();

        File file = resource.file;
        logger.info("parseChild: " + file);
        FileFrame childFrame = new FileFrame(parent, file);

        // XXX
        this.extension = childFrame.getExtension();
        this.lang = SrcLangType.forExtension(extension);
        this.opener = lang.opener;
        this.closer = lang.closer;
        this.simpleOpener = lang.simpleOpener;

        scriptContext.put(VAR_FRAME, childFrame);

        LineReader lineReader = resource.toResource().newLineReader();
        try {
            parse(childFrame, lineReader);
        } finally {
            lineReader.close();
        }
    }

}
