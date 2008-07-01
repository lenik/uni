package net.bodz.lapiota.programs;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;
import net.bodz.lapiota.ant.tasks.ProgramName;

@Doc("Copy parts of file")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("jargs")
public class CLIEchoArgs extends BasicCLI {

    @Override
    protected void _main(String[] args) throws Throwable {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            _PF("%4d. %s;", i, arg);
        }
    }

    public static void main(String[] args) throws Throwable {
        new CLIEchoArgs().climain(args);
    }

}
