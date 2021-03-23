package net.bodz.uni.catme.io;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface IWatcherCallback {

    default void onEvent(WatcherWait watcher, Path dir, WatchEvent<?> event) {
    }

    default void onCreate(WatcherWait watcher, Path file) {
    }

    default void onDelete(WatcherWait watcher, Path file) {
    }

    default void onModify(WatcherWait watcher, Path file) {
    }

}
