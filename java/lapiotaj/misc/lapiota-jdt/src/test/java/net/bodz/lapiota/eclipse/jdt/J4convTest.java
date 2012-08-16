package net.bodz.lapiota.eclipse.jdt;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import net.bodz.bas.cli.boot.win32.Mkbat;
import net.bodz.bas.snm.EclipseProject;

public class J4convTest {

    public void testJ4conv()
            throws Throwable {
        String tmpdir = System.getenv("TEMP");
        File outdir = new File(tmpdir, "testj4conv");
        try {
            outdir.mkdirs();

            URL srcurl = EclipseProject.getSrcURL(J4conv.class);
            String srcfile = srcurl.getFile();
            int reslen = J4conv.class.getName().length() + ".java".length();
            String srcdir = srcfile.substring(0, srcfile.length() - reslen);

            // XXX must use a separate java.exe to get a clean class loader.
            Mkbat gl = new Mkbat();

            gl.execute("-r", "-O", outdir.toString(), srcdir);

        } finally {
            Files.deleteTree(outdir);
        }
    }

    public static void main(String[] args)
            throws Throwable {
        new J4convTest().testJ4conv();
    }

}
