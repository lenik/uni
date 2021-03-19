package net.bodz.uni.catme;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface IWatcherCallback {

    default void onEvent(Path dir, WatchEvent<?> event) {
    }

    default void onCreate(Path file) {
    }

    default void onDelete(Path file) {
    }

    default void onModify(Path file) {
    }

}
