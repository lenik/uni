package net.bodz.uni.catme;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.nio.Charsets;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.bas.io.ICharIn;
import net.bodz.bas.io.StringCharIn;
import net.bodz.bas.io.res.IStreamResource;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.script.IScriptContext;
import net.bodz.bas.script.io.ResourceResolver;
import net.bodz.bas.script.io.ResourceVariant;
import net.bodz.bas.text.qlex.trie.TrieLexer;

public class FileFrame
        extends AbstractFrame {

    static final Logger logger = LoggerFactory.getLogger(FileFrame.class);

    File file;
    Charset charset = Charsets.UTF8;

    String extension;
    SrcLangType lang;
    String opener;
    String closer;
    String simpleOpener;
    String escapePrefix = "\\";

    TrieLexer<MySym> lexer;
    TrieLexer<MySym> commentLexer;
    TrieLexer<MySym> instructionLexer;

    IScriptContext scriptContext;

    public FileFrame(MainParser parser, File file) {
        this(null, parser, file);
        allocateImportSet();
    }

    public FileFrame(IFrame parent, MainParser parser, File file) {
        super(parent, parser);
        this.file = file;

        this.extension = FilePath.getExtension(file);
        this.lang = SrcLangType.forExtension(extension);
        this.opener = lang.opener;
        this.closer = lang.closer;
        this.simpleOpener = lang.simpleOpener;

        lexer = new TrieLexer<MySym>();
        if (opener != null)
            lexer.declare(opener, MySym.OPENER);
        if (simpleOpener != null)
            lexer.declare(simpleOpener, MySym.SL_OPENER);
        // lexer.declare("\n", MySym.NEWLINE);

        commentLexer = new TrieLexer<MySym>();
        if (closer != null)
            commentLexer.declare(closer, MySym.CLOSER);

        instructionLexer = new TrieLexer<MySym>();
        if (escapePrefix != null)
            instructionLexer.declare(escapePrefix, MySym.ESCAPE);
    }

    @Override
    public boolean isFile() {
        return true;
    }

    ResourceVariant resolveHref(String href, boolean user)
            throws IOException {
        File join = FilePath.joinHref(file, href);
        if (join.exists())
            return new ResourceVariant(join);

        MainParser parser = getParser();
        ResourceResolver resolver = user ? parser.app.userResolver : parser.app.scriptResolver;
        ResourceVariant resource = resolver.findResource(href);
        if (resource != null)
            return resource;

        return super.resolveHref(href);
    }

    @Override
    public ResourceVariant resolveHref(String href)
            throws IOException {
        return resolveHref(href, true);
    }

    @Override
    public ResourceVariant resolveQName(String qName)
            throws IOException {
        if (qName == null)
            throw new NullPointerException("qName");
        String href = qName.replace('.', '/') + "." + extension;
        return resolveHref(href);
    }

    @Override
    public ResourceVariant resolveModule(String module)
            throws IOException {
        if (module == null)
            throw new NullPointerException("module");
        String href = module.replace('.', '/') + ".js";
        return resolveHref(href, false);
    }

    public boolean initScript() {
        if (scriptContext != null)
            return true;

        if (!parser.initScriptContext())
            return false;

        IScriptContext scriptContext = parser.getScriptContext();
        scriptContext.put(IFrame.VAR_FRAME, this);

        try {
            scriptContext.include("./js/fileArg.js");
        } catch (Exception e) {
            logger.error("Failed to prepare file " + getPath() + ": " + e.getMessage(), e);
            return false;
        }
        this.scriptContext = scriptContext;
        return true;
    }

    public IScriptContext getScriptContext() {
        if (!initScript())
            throw new IllegalStateException("Script not ready.");
        return scriptContext;
    }

    public Object eval(String code)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        return getScriptContext().eval(code);
    }

    public Object eval(String code, String fileName)
            throws EvalException, IOException {
        if (code == null)
            throw new NullPointerException("code");
        return getScriptContext().eval(code, fileName);
    }

    public void parse()
            throws IOException, ParseException {
        parse(new FileResource(file, charset));
    }

    public void parse(IStreamResource resource)
            throws IOException, ParseException {
        Object oldFrame = null;
        if (scriptContext != null) {
            oldFrame = scriptContext.get(VAR_FRAME);
            scriptContext.put(VAR_FRAME, this);
        }

        ICharIn in = null;
        try {
            // in = resource.newCharIn();
            String text = resource.to(StreamReading.class).readString();
            in = new StringCharIn(text);
            parser.parseFrameSource(this, in);
        } finally {
            if (in != null)
                in.close();
            if (scriptContext != null)
                scriptContext.put(VAR_FRAME, oldFrame);
        }
    }

    @Override
    public void parse(String href)
            throws IOException, ParseException {
        ResourceVariant resource = resolveHref(href);
        if (resource == null)
            throw new IllegalArgumentException("Can't resolve file: " + href);
        parse(resource.toResource());
    }

    public String getPath() {
        return file.getPath();
    }

    public String getDirName() {
        String path = getPath();
        return FilePath.getDirName(path);
    }

    public String getBaseName() {
        String path = getPath();
        return FilePath.getBaseName(path);
    }

    public String getExtension() {
        return FilePath.getExtension(file);
    }

    public String getExtensionWithDot() {
        return FilePath.getExtension(file, true);
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

    @Override
    public String toString() {
        return file.getPath() + ":" + currentLine + ":" + currentColumn;
    }

}
