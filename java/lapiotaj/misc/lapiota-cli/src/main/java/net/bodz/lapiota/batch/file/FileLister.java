package net.bodz.lapiota.batch.file;

import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.cli.skel.BatchCLI;
import net.bodz.bas.cli.skel.FileHandler;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.vfs.IFile;

/**
 * A simple BatchProcessCLI example: dir in java
 */
@ProgramName("jdir")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FileLister
        extends BatchCLI {

    @Override
    public void processFile(FileHandler handler)
            throws Exception {
        IFile file = handler.getInputFile();
        System.out.println(file);
    }

    public static void main(String[] args)
            throws Exception {
        new FileLister().execute(args);
    }

}
