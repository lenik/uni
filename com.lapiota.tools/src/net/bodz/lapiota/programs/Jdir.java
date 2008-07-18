package net.bodz.lapiota.programs;

import java.io.File;
import java.io.InputStream;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.lapiota.util.BatchProcessCLI;

@Doc("A Unix diff program implemented in Java")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class Jdir extends BatchProcessCLI {

    @Override
    protected InputStream _getDefaultIn() {
        return null;
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
