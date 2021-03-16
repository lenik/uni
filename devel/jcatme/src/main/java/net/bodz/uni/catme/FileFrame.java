package net.bodz.uni.catme;

import java.io.File;

import net.bodz.bas.c.java.io.FilePath;

public class FileFrame
        extends Frame {

    File file;

    public FileFrame(File file) {
        this(null, file);
    }

    public FileFrame(Frame parent, File file) {
        super(parent);
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
