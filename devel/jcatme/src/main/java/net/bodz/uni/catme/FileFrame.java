package net.bodz.uni.catme;

import java.io.File;

import net.bodz.bas.c.java.io.FilePath;

public class FileFrame
        extends AbstractFrame {

    File file;

    public FileFrame(MainParser parser, File file) {
        this(null, parser, file);
    }

    public FileFrame(IFrame parent, MainParser parser, File file) {
        super(parent, parser);
        this.file = file;
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
