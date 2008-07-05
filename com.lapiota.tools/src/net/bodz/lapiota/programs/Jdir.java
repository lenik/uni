package net.bodz.lapiota.programs;

import java.io.File;

import net.bodz.bas.cli.BatchProcessCLI;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;

@Doc("A Unix diff program implemented in Java")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class Jdir extends BatchProcessCLI {

    @Override
    protected int _cliflags() {
        return super._cliflags() & ~(CLI_AUTOSTDIN | CLI_BATCHEDIT);
    }

    @Override
    protected ProcessResult process(File file) throws Throwable {
        System.out.println(file);
        return null;
    }

    public static void main(String[] args) throws Throwable {
        new Jdir().climain(args);
    }

}
