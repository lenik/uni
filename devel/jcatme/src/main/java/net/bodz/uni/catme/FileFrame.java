package net.bodz.uni.catme;

import java.io.File;
import java.io.IOException;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.uni.catme.io.ResourceVariant;

public class FileFrame
        extends AbstractFrame {

    File file;

    String extension;
    SrcLangType lang;
    String opener;
    String closer;
    String simpleOpener;
    String escapePrefix = "\\";

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
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public ResourceVariant resolveHref(String href)
            throws IOException {
        MainParser parser = getParser();
        ResourceVariant resource = parser.resourceResolver.findResource(href);
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

}
