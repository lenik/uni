package net.bodz.uni.catme.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.io.res.AbstractStreamResource;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.URLResource;

public class ResourceVariant {

    public static final int FILE = 1;
    public static final int URL = 2;
    public static final int TEXT = 3;
    public static final int BIN = 4;

    public final int type;
    public File file;
    public URL url;
    public String text;
    public byte[] bin;

    public ResourceVariant(File file) {
        this.type = FILE;
        this.file = file;
    }

    public ResourceVariant(URL url) {
        this.type = URL;
        this.url = url;
    }

    public ResourceVariant(String text) {
        this.type = TEXT;
        this.text = text;
    }

    public ResourceVariant(byte[] bin) {
        this.type = BIN;
        this.bin = bin;
    }

    public AbstractStreamResource toResource() {
        switch (type) {
        case FILE:
            return new FileResource(file);
        case URL:
            return new URLResource(url);
        }
        throw new NotImplementedException();
    }

    public Path toPath() {
        switch (type) {
        case FILE:
            return file.toPath();
        case URL:
            URI uri;
            try {
                uri = url.toURI();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            File uriFile = new File(uri);
            return uriFile.toPath();
        }
        throw new NotImplementedException();
    }

    public ResourceVariant resolve(String spec) {
        switch (type) {
        case FILE:
            File otherFile = new File(file, spec);
            return new ResourceVariant(otherFile);
        case URL:
            URL otherUrl;
            try {
                otherUrl = new URL(url, spec);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return new ResourceVariant(otherUrl);
        }
        throw new NotImplementedException();
    }

}
