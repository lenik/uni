package net.bodz.uni.catme.io;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import net.bodz.bas.c.java.io.capture.Processes;
import net.bodz.bas.c.java.time.DateTimes;
import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class LoopRunner
        implements
            IWatcherCallback {

    static final Logger logger = LoggerFactory.getLogger(LoopRunner.class);

    Runnable runnable;
    WatcherWait watcher;

    int iteration = 0;

    public LoopRunner(Runnable runnable, File... dirs)
            throws IOException {
        this(runnable, Arrays.asList(dirs));
    }

    public LoopRunner(Runnable runnable, Collection<File> dirs)
            throws IOException {
        if (runnable == null)
            throw new NullPointerException("runnable");
        this.runnable = runnable;
        this.watcher = new WatcherWait(this);

        for (File dir : dirs)
            watchDir(dir);
    }

    public WatcherWait getWatcher() {
        return watcher;
    }

    public void watchDir(File file)
            throws IOException {
        watcher.addDirectory(file);
    }

    public void watchDir(Path path)
            throws IOException {
        watcher.addDirectory(path);
    }

    public void run() {
        watcher.run();
    }

    Duration lastDuration;
    ZonedDateTime startTime;

    @Override
    public void onEvent(WatcherWait watcher, Path dir, WatchEvent<?> event) {
        if (event == OVERFLOW)
            return;

        Path name = (Path) event.context();
        Path path = dir.resolve(name);
        logger.debug("File changed: " + path + ", at " + new Date());

        printSep();

        this.startTime = ZonedDateTime.now();
        {
            runnable.run();
        }
        Duration duration = Duration.between(startTime, ZonedDateTime.now());
        this.lastDuration = duration;

        watcher.ignoreForAwhile();
    }

    void printSep() {
        int cols = getColumns();
        String time = DateTimes.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now());
        Instant durationTime = Instant.ofEpochSecond(lastDuration.getSeconds());
        String lastDuration = DateTimes.ISO_LOCAL_TIME.format(durationTime);
        String label = String.format("_%d_ %s ^ %s", ++iteration, time, lastDuration);
        int n = label.length();

        int half = (cols - n) / 2;
        String pads = Strings.repeat(half, '-');
        System.err.println(pads + label + pads);
    }

    static final int COLS_NA = 80;
    static final int COLS_ERROR = COLS_NA;

    public static int getColumns() {
        try {
            Process process = Processes.exec("tput", "cols");
            String result = Processes.iocap(process, "utf-8");

            if (StringPred.isDecimal(result)) {
                int columns = Integer.valueOf(result);
                return columns;
            } else {
                return COLS_NA;
            }
        } catch (Exception e) {
            logger.error("Can't find out the columns: " + e.getMessage(), e);
            return COLS_ERROR;
        }
    }

}
