package net.bodz.lapiota.filesys;

import java.io.File;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.lapiota.a.ProgramName;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("A simple BatchProcessCLI example: dir in java")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@ProgramName("jdir")
public class FileLister extends BatchProcessCLI {

    @Override
    protected ProcessResult doFile(File file) throws Throwable {
        System.out.println(file);
        return null;
    }

    public static void main(String[] args) throws Throwable {
        new FileLister().run(args);
    }

}
