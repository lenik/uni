package net.bodz.lapiota.programs;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.lapiota.util.BasicCLI;
import net.bodz.lapiota.util.ProgramName;

@Doc("Dump arguments for CLI program")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("jargs")
public class CLIEchoArgs extends BasicCLI {

    @Override
    protected void _main(String[] args) throws Throwable {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            L.m.PF("%4d. %s;", i, arg);
        }
    }

    public static void main(String[] args) throws Throwable {
        new CLIEchoArgs().climain(args);
    }

}
