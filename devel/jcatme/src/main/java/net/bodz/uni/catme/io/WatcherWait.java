package net.bodz.uni.catme.io;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class WatcherWait
        implements
            Runnable {

    static final Logger logger = LoggerFactory.getLogger(WatcherWait.class);

    final Set<Path> excludeFiles = new HashSet<>();
    final List<Pattern> excludePatterns = new ArrayList<>();

    // not implemented
    boolean recursive;

    int timeout;
    List<WatchEvent.Kind<Path>> events = new ArrayList<>();
    IWatcherCallback callback;

    FileSystem fileSystem;
    WatchService watcher;
    final Set<Path> files = new HashSet<>();
    final Map<WatchKey, Path> keyMap = new HashMap<>();

    public static final int DEFAULT_AWHILE = 100;
    long ignoreUntil;

    @SuppressWarnings("unchecked")
    private static final WatchEvent.Kind<Path>[] EMPTY_KINDS = new WatchEvent.Kind[0];

    public WatcherWait(IWatcherCallback callback) {
        // this(EMPTY_KINDS);
        this(callback, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
    }

    @SafeVarargs
    public WatcherWait(IWatcherCallback callback, WatchEvent.Kind<Path>... events) {
        if (callback == null)
            throw new NullPointerException("callback");
        if (events == null)
            throw new NullPointerException("events");
        this.callback = callback;
        for (WatchEvent.Kind<Path> event : events)
            this.events.add(event);
    }

    public void addFile(File file)
            throws IOException {
        Path path = file.toPath();
        addFile(path);
    }

    public void addFile(Path path)
            throws IOException {
        if (recursive)
            registerAll(path);
        else
            register(path);
    }

    void register(Path dir)
            throws IOException {
        if (dir == null)
            throw new NullPointerException("path");
        FileSystem fs = dir.getFileSystem();
        if (fs == null)
            throw new NullPointerException("null filesystem from file.");

        if (fileSystem == null) {
            fileSystem = fs;
            watcher = fileSystem.newWatchService();
        } else {
            if (!fs.equals(fileSystem))
                throw new IllegalArgumentException("Not in a same filesystem.");
        }
        // same fs.
        this.files.add(dir);

        Kind<Path>[] kindv = events.toArray(EMPTY_KINDS);
        WatchKey key = dir.register(watcher, kindv);

        Path prev = keyMap.put(key, dir);
        if (prev == null)
            logger.debug("register: " + dir);
        else
            logger.debug("update: " + prev + " -> " + dir);
    }

    void registerAll(final Path start)
            throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        for (;;) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            Path dir = keyMap.get(key);
            if (dir == null) {
                logger.error("WatchKey not recognized!!");
                return;
            }

            process(key, dir);

            boolean valid = key.reset();
            if (!valid) {
                keyMap.remove(key);
                if (keyMap.isEmpty())
                    break;
            }
        }
    }

    boolean process(WatchKey key, Path dir) {
        List<WatchEvent<?>> events = key.pollEvents();
        int n = events.size();

        boolean ignored = shouldIgnore();

        for (int i = 0; i < n; i++) {
            WatchEvent<?> event = events.get(i);
            WatchEvent.Kind<?> kind = event.kind();
            if (kind == OVERFLOW)
                continue;
            Path name = (Path) event.context();
            Path child = dir.resolve(name);

            // print out event
            logger.tracef("event %d/%d %s: %s", i + 1, n, event.kind().name(), child);

            if (recursive && (kind == ENTRY_CREATE))
                try {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS))
                        registerAll(child);
                } catch (IOException x) {
                    // ignore to keep sample readbale
                }

            if (ignored)
                logger.trace("    ignored.");
            else {
                if (callback != null) {
                    callback.onEvent(this, dir, event);
                    if (kind == ENTRY_CREATE) {
                        callback.onCreate(this, child);
                    } else if (kind == ENTRY_DELETE) {
                        callback.onDelete(this, child);
                    } else if (kind == ENTRY_MODIFY) {
                        callback.onModify(this, child);
                    }
                }
            }

            ignored = shouldIgnore();
        } // for events
        return true;
    }

    public void ignoreForAwhile() {
        ignoreForAwhile(DEFAULT_AWHILE);
    }

    public void ignoreForAwhile(int ms) {
        ignoreUntil = System.currentTimeMillis() + ms;
    }

    private boolean shouldIgnore() {
        if (ignoreUntil != 0)
            if (System.currentTimeMillis() < ignoreUntil)
                return true;
        return false;
    }

}