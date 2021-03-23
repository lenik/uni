package net.bodz.uni.catme;

import java.io.File;
import java.io.IOException;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.ICharIn;
import net.bodz.bas.io.res.IStreamResource;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.uni.catme.io.ResourceVariant;
import net.bodz.uni.catme.trie.TrieLexer;

public class FileFrame
        extends AbstractFrame {

    File file;

    String extension;
    SrcLangType lang;
    String opener;
    String closer;
    String simpleOpener;
    String escapePrefix = "\\";

    TrieLexer<MySym> lexer;
    TrieLexer<MySym> commentLexer;
    TrieLexer<MySym> instructionLexer;

    public FileFrame(MainParser parser, File file) {
        this(null, parser, file);
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
            lexer.declare(opener, new MySym(MySym.OPENER, "opener"));
        if (simpleOpener != null)
            lexer.declare(simpleOpener, new MySym(MySym.SIMPLE_OPENER, "simpleOpener"));

        commentLexer = new TrieLexer<MySym>();
        if (closer != null)
            commentLexer.declare(closer, new MySym(MySym.CLOSER, "closer"));

        instructionLexer = new TrieLexer<MySym>();
        if (escapePrefix != null)
            instructionLexer.declare(escapePrefix, new MySym(MySym.ESCAPE, "escape"));
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public ResourceVariant resolveHref(String href)
            throws IOException {
        File join = FilePath.joinHref(file, href);
        if (join.exists())
            return new ResourceVariant(join);

        MainParser parser = getParser();
        ResourceVariant resource = parser.app.userResolver.findResource(href);
        if (resource != null)
            return resource;

        return super.resolveHref(href);
    }

    @Override
    public ResourceVariant resolveQName(String qName)
            throws IOException {
        if (qName == null)
            throw new NullPointerException("qName");
        String href = qName.replace('.', '/') + "." + extension;
        return resolveHref(href);
    }

    public void parse()
            throws IOException, ParseException {
        parse(new FileResource(file));
    }

    public void parse(IStreamResource resource)
            throws IOException, ParseException {
        Object oldFrame = parser.scriptContext.get(VAR_FRAME);
        parser.scriptContext.put(VAR_FRAME, this);

        ICharIn in = resource.newCharIn();
        try {
            parser.parse(this, in);
        } finally {
            in.close();
            parser.scriptContext.put(VAR_FRAME, oldFrame);
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
        return file.getPath();
    }

}
