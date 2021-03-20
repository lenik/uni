package net.bodz.uni.catme;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Date;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.catme.io.IWatcherCallback;
import net.bodz.uni.catme.io.WatcherWait;

/**
 * CatMe rewritten in Java.
 */
@MainVersion({ 0, 1 })
@ProgramName("jcatme")
@RcsKeywords(id = "$Id$")
public class CatMe
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(CatMe.class);

    String[] args;

    public CatMe() {
    }

    void runOnce() {
        try {
            MainParser parser;
            try {
                parser = new MainParser(this);
            } catch (Exception e) {
                logger.error("Failed to instantiate parser: " + e.getMessage(), e);
                return;
            }
            for (String arg : args) {
                File file = new File(arg);
                if (file.exists()) {
                    parser.parseFile(file);
                    continue;
                }
                throw new IllegalArgumentException("invalid argument: " + arg);
            }
        } catch (Exception e) {
            logger.error("Failed to parse: " + e.getMessage(), e);
        }
    }

    @Override
    protected void mainImpl(final String... args)
            throws Exception {
        this.args = args;

        WatcherWait watcherWait = new WatcherWait(new IWatcherCallback() {

            @Override
            public boolean onEvent(Path dir, WatchEvent<?> event) {
                if (event == OVERFLOW)
                    return false;

                Path name = (Path) event.context();
                Path path = dir.resolve(name);
                logger.info("File changed: " + path + ", at " + new Date());

                runOnce();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return true; // cancel
            }
        });

        File watchDir = new File("/home/lenik/pro/uni/devel/jcatme/src/main/resources/js");
        watcherWait.addFile(watchDir);
        watcherWait.addFile(new File(watchDir, "builtins"));

        runOnce();
        watcherWait.run();
    }

    public static void main(String[] args)
            throws Exception {
        new CatMe().execute(args);
    }

}
