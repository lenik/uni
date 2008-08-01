package net.bodz.lapiota.filesys;

import java.io.File;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.lapiota.annotations.ProgramName;
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
        new FileLister().climain(args);
    }

}
