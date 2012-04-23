package net.bodz.lapiota.filesys;

import net.bodz.bas.cli.BatchCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;

/**
 * A simple BatchProcessCLI example: dir in java
 */
@ProgramName("jdir")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class FileLister
        extends BatchCLI {

    @Override
    protected void doFile(IFile file)
            throws Exception {
        System.out.println(file);
    }

    public static void main(String[] args)
            throws Exception {
        new FileLister().run(args);
    }

}
