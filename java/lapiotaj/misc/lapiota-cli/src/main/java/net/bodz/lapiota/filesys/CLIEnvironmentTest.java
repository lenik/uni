package net.bodz.lapiota.filesys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.util.order.ComparableComparator;

/**
 * Dump arguments for CLI program
 */
@MainVersion({ 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("jenv")
public class CLIEnvironmentTest
        extends BasicCLI {

    /**
     * @option -E
     */
    void dumpEnv() {
        Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for (Object key : keys) {
            String value = env.get(key);
            L.mesg(key, " = ", value);
        }
    }

    /**
     * @option -P
     */
    void dumpProperties() {
        Properties properties = System.getProperties();
        List<Object> keys = new ArrayList<Object>(properties.keySet());
        Collections.sort(keys, ComparableComparator.getRawInstance());
        for (Object key : keys) {
            Object value = properties.get(key);
            L.mesg(key, " = ", value);
        }
    }

    protected void _main(String[] args)
            throws Throwable {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            L.mesgf("%4d. %s;", i, arg);
        }
    }

    public static void main(String[] args)
            throws Throwable {
        new CLIEnvironmentTest().execute(args);
    }

}
