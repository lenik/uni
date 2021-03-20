package net.bodz.uni.catme.io;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface IWatcherCallback {

    default boolean onEvent(Path dir, WatchEvent<?> event) {
        return false;
    }

    default boolean onCreate(Path file) {
        return false;
    }

    default boolean onDelete(Path file) {
        return false;
    }

    default boolean onModify(Path file) {
        return false;
    }

}
