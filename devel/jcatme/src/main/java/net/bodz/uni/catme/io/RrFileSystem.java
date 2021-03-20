package net.bodz.uni.catme.io;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.io.FileSystem;

public class RrFileSystem
        implements
            FileSystem {

    ResourceResolver rr;

    public RrFileSystem(ResourceResolver rr) {
        this.rr = rr;
    }

    @Override
    public Path parsePath(URI uri) {
        String path = uri.toString();
        return parsePath(path);
    }

    @Override
    public Path parsePath(String path) {
        ResourceVariant res;
        try {
            res = rr.findResource(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (res == null)
            throw new IllegalArgumentException("Not found: " + path);
        return res.toPath();
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions)
            throws IOException {
        AccessMode[] modev = modes.toArray(new AccessMode[0]);
        path.getFileSystem().provider().checkAccess(path, modev);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs)
            throws IOException {
        Files.createDirectories(dir, attrs);
    }

    @Override
    public void delete(Path path)
            throws IOException {
        Files.delete(path);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
            throws IOException {
        return Files.newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
            throws IOException {
        return Files.newDirectoryStream(dir, filter);
    }

    @Override
    public Path toAbsolutePath(Path path) {
        return path.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions)
            throws IOException {
        return path.toRealPath(linkOptions);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
            throws IOException {
        return Files.readAttributes(path, attributes, options);
    }

}
