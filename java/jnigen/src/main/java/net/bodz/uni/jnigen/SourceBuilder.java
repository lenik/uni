package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;

import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.res.builtin.FileResource;

public abstract class SourceBuilder {

    File showFileNameAtBegin;

    public void showFileNameAtBegin(File fileName) {
        showFileNameAtBegin = fileName;
    }

    protected String buildPreamble() {
        StringBuilder preamble = new StringBuilder();
        if (showFileNameAtBegin != null)
            preamble.append("/** FILE: " + showFileNameAtBegin + " */");
        return preamble.toString();
    }

    public void buildSource(File file)
            throws IOException {
        File parent = file.getParentFile();
        parent.mkdirs();
        if (!parent.exists() || !parent.isDirectory())
            throw new IOException("Can't create parent directory: " + parent);

        // System.out.println("Generate " + file);
        try (ITreeOut out = new FileResource(file).newTreeOut()) {
            buildSource(out, file);
        }
    }

    public abstract void buildSource(ITreeOut out, File file)
            throws IOException;

}
