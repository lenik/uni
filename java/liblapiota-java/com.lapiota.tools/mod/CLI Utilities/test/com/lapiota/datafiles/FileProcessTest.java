package com.lapiota.datafiles;

import static net.bodz.bas.test.TestDefs.END;
import static net.bodz.bas.test.TestDefs.EQ;

import java.io.File;

import net.bodz.bas.cli.EditResult;
import net.bodz.bas.test.TestDefs;
import net.bodz.bas.test.TestEval;

import org.junit.Test;

import com.lapiota.datafiles.FileProcess;

public class FileProcessTest extends FileProcess {

    static class RenCompEval implements TestEval<String> {

        @Override
        public Object eval(String input) throws Exception {
            String[] args = input.split("\\|", 2); //$NON-NLS-1$
            final String file = args[0].trim();
            final String repl = args[1].trim();
            final EditResult[] result = new EditResult[1];
            new FileProcess() {
                @Override
                protected void doMain(String[] args) throws Exception {
                    RenameComponents renAction = (RenameComponents) actions.get(0);
                    result[0] = renAction.run(new File(file), null, null);
                }
            }.run("-Dnonexist=X", "-asg=" + repl); //$NON-NLS-1$ //$NON-NLS-2$
            File dst = (File) result[0].dest;
            return dst.getPath();
        }

    }

    @Test
    public void testRenameComponents() {
        TestDefs.tests(new RenCompEval(), //
                EQ("[a][b][c].ext | $1-$2-$3", "a-b-c.ext"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("[a][b][c].ext | $3$2$1", "cba.ext"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ(" [a] [b] [c] .ext | ${1}ok-$4", "aok-X.ext"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("pre [sqr] i (par) j {bra} k kk - dash.ext | $1$2$3-$7$8$9", // //$NON-NLS-1$
                        "presqri-k kkdashX.ext"), // //$NON-NLS-1$
                EQ(" - a - b - c - -- d | $1-$2-$3-$4-$5-$6-$7", // //$NON-NLS-1$
                        "a-b-c-d-X-X-X"), // //$NON-NLS-1$
                END);
    }

}
