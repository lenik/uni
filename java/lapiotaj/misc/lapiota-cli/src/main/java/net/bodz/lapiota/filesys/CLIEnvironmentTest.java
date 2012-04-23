package net.bodz.lapiota.filesys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.info.Doc;
import net.bodz.bas.meta.program.ProgramName;

@Doc("Dump arguments for CLI program")
@Version({ 0, 1 })
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
            L.m.P(key, " = ", value);
        }
    }

    /**
     * @option -P
     */
    void dumpProperties() {
        Properties properties = System.getProperties();
        List<Object> keys = new ArrayList<Object>(properties.keySet());
        Collections.sort(keys, Comparators.STD);
        for (Object key : keys) {
            Object value = properties.get(key);
            L.m.P(key, " = ", value);
        }
    }

    @Override
    protected void _main(String[] args)
            throws Throwable {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            L.m.PF("%4d. %s;", i, arg);
        }
    }

    public static void main(String[] args)
            throws Throwable {
        new CLIEnvironmentTest().climain(args);
    }

}
