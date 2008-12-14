package net.bodz.lapiota.filesys;

import java.io.File;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.lapiota.wrappers.BatchCLI;

@Doc("A simple BatchProcessCLI example: dir in java")
@ProgramName("jdir")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class FileLister extends BatchCLI {

    @Override
    protected void doFile(File file) throws Throwable {
        System.out.println(file);
    }

    public static void main(String[] args) throws Throwable {
        new FileLister().run(args);
    }

}
