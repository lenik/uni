package net.bodz.lapiota.datafiles;

import static net.bodz.bas.test.TestDefs.END;
import static net.bodz.bas.test.TestDefs.EQ;

import java.io.File;

import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.lang.ref.Ref;
import net.bodz.bas.lang.ref.SimpleRef;
import net.bodz.bas.test.TestDefs;
import net.bodz.bas.test.TestEval;
import net.bodz.lapiota.datafiles.FileProcess;

import org.junit.Test;

public class FileProcessTest extends FileProcess {

    static class RenCompEval implements TestEval<String> {

        @Override
        public Object eval(String input) throws Throwable {
            String[] args = input.split("\\|", 2);
            final String file = args[0].trim();
            final String repl = args[1].trim();
            final ProcessResult[] result = new ProcessResult[1];
            new FileProcess() {
                @Override
                protected void _main(String[] args) throws Throwable {
                    RenameComponents ren = (RenameComponents) actions.get(0);
                    result[0] = ren.run(new File(file), null, null);
                }
            }.run("-Dnonexist=X", "-asg=" + repl);
            File dst = (File) result[0].dest;
            return dst.getPath();
        }

    }

    @Test
    public void testRenameComponents() {
        TestDefs.tests(new RenCompEval(), //
                EQ("[a][b][c].ext | $1-$2-$3", "a-b-c.ext"), //
                EQ("[a][b][c].ext | $3$2$1", "cba.ext"), //
                EQ(" [a] [b] [c] .ext | ${1}ok-$4", "aok-X.ext"), //
                EQ("pre [sqr] i (par) j {bra} k kk - dash.ext | $1$2$3-$7$8$9", //
                        "presqri-k kkdashX.ext"), //
                EQ(" - a - b - c - -- d | $1-$2-$3-$4-$5-$6-$7", //
                        "a-b-c-d-X-X-X"), //
                END);
    }

    public static void main(String[] args) throws Throwable {
        new FileProcessTest().testRenameComponents();
    }

}
