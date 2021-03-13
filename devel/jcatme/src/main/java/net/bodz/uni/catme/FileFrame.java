package net.bodz.uni.catme;

import java.io.File;

import net.bodz.bas.c.java.io.FilePath;

public class FileFrame
        extends Frame {

    File file;

    SrcLangType lang;
    String opener;
    String closer;
    String simpleOpener;

    public FileFrame(File file) {
        this.file = file;
        String extension = getExtension();
        lang = SrcLangType.forExtension(extension);
        opener = lang.opener;
        closer = lang.closer;
        simpleOpener = lang.simpleOpener;
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

}
