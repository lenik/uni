package net.bodz.lapiota.eclipse.jdt;

import java.io.File;

import net.bodz.bas.c.m2.MavenProjectOrigin;
import net.bodz.bas.cli.boot.win32.Mkbat;

public class J4convTest {

    public void testJ4conv()
            throws Throwable {
        String tmpdir = System.getenv("TEMP");
        File outdir = new File(tmpdir, "testj4conv");
        try {
            outdir.mkdirs();

            MavenProjectOrigin po = MavenProjectOrigin.fromClass(J4conv.class);
            File srcfile = po.getSourceFile(J4conv.class);
            String srcpath = srcfile.getPath();

            int reslen = J4conv.class.getName().length() + ".java".length();
            String srcdir = srcpath.substring(0, srcpath.length() - reslen);

            // XXX must use a separate java.exe to get a clean class loader.
            Mkbat gl = new Mkbat();

            gl.execute("-r", "-O", outdir.toString(), srcdir);

        } finally {
            outdir.deleteTree();
        }
    }

    public static void main(String[] args)
            throws Throwable {
        new J4convTest().testJ4conv();
    }

}
