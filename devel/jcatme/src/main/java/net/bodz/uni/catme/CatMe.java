package net.bodz.uni.catme;

import java.io.File;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * CatMe rewritten in Java.
 */
@MainVersion({ 0, 1 })
@ProgramName("jcatme")
@RcsKeywords(id = "$Id$")
public class CatMe
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        MainParser parser = new MainParser();
        for (String arg : args) {
            File file = new File(arg);
            if (file.exists()) {
                parser.parse(file);
                continue;
            }
            throw new IllegalArgumentException("invalid argument: " + arg);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new CatMe().execute(args);
    }

}
