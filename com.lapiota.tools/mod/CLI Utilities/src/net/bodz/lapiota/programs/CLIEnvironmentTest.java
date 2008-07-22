package net.bodz.lapiota.programs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.types.util.Comparators;
import net.bodz.lapiota.annotations.ProgramName;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("Dump arguments for CLI program")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("jenv")
public class CLIEnvironmentTest extends BasicCLI {

    @Option(alias = "E")
    void dumpEnv() {
        Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for (Object key : keys) {
            String value = env.get(key);
            L.m.P(key, " = ", value);
        }
    }

    @Option(alias = "P")
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
    protected void _main(String[] args) throws Throwable {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            L.m.PF("%4d. %s;", i, arg);
        }
    }

    public static void main(String[] args) throws Throwable {
        new CLIEnvironmentTest().climain(args);
    }

}
