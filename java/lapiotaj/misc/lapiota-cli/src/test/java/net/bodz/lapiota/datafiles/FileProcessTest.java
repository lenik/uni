package net.bodz.lapiota.datafiles;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.VFS;

public class FileProcessTest
        extends Assert {

    @Test
    public void testRenameComponents()
            throws Exception {
        class D {
            void o(String input, String expectedPath)
                    throws Exception {
                String[] args = input.split("\\|", 2);
                String path = args[0].trim();
                final IFile file = VFS.resolve(path);

                final String repl = args[1].trim();
                final EditResult[] result = new EditResult[1];
                new FileProcess() {
                    @Override
                    protected void doMain(String[] args)
                            throws Exception {
                        RenameComponents renAction = (RenameComponents) actions.get(0);
                        result[0] = renAction.run(file, null, null);
                    }
                }.execute("-Dnonexist=X", "-asg=" + repl);
                File dst = (File) result[0].dest;
                assertEquals(expectedPath, dst.getPath());
            }
        }
        D d = new D();
        d.o("[a][b][c].ext | $1-$2-$3", "a-b-c.ext");
        d.o("[a][b][c].ext | $3$2$1", "cba.ext");
        d.o(" [a] [b] [c] .ext | ${1}ok-$4", "aok-X.ext");
        d.o("pre [sqr] i (par) j {bra} k kk - dash.ext | $1$2$3-$7$8$9", "presqri-k kkdashX.ext");
        d.o(" - a - b - c - -- d | $1-$2-$3-$4-$5-$6-$7", "a-b-c-d-X-X-X");
    }

}
