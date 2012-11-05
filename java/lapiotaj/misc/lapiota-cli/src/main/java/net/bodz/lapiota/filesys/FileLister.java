package net.bodz.lapiota.filesys;

import net.bodz.bas.cli.skel.BatchCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;

/**
 * A simple BatchProcessCLI example: dir in java
 */
@ProgramName("jdir")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FileLister
        extends BatchCLI {

    public static void main(String[] args)
            throws Exception {
        new FileLister().execute(args);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        // TODO: File scanner after expanded the wildcards.
        for (IFile file : expandWildcards(args))
            System.out.println(file);
        ;
    }

}
