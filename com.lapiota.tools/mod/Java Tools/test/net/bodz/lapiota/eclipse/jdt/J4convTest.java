package net.bodz.lapiota.eclipse.jdt;

import java.io.File;
import java.net.URL;

import net.bodz.bas.cli.util.Mkbat;
import net.bodz.bas.io.Files;
import net.bodz.bas.snm.SJProject;

public class J4convTest {

    public void testJ4conv() throws Throwable {
        String tmpdir = System.getenv("TEMP"); //$NON-NLS-1$
        File outdir = new File(tmpdir, "testj4conv"); //$NON-NLS-1$
        try {
            outdir.mkdirs();

            URL srcurl = SJProject.getSrcURL(J4conv.class);
            String srcfile = srcurl.getFile();
            int reslen = J4conv.class.getName().length() + ".java".length(); //$NON-NLS-1$
            String srcdir = srcfile.substring(0, srcfile.length() - reslen);

            // XXX must use a separate java.exe to get a clean class loader.
            Mkbat gl = new Mkbat();

            gl.run("-r", "-O", outdir.toString(), srcdir); //$NON-NLS-1$ //$NON-NLS-2$

        } finally {
            Files.deleteTree(outdir);
        }
    }

    public static void main(String[] args) throws Throwable {
        new J4convTest().testJ4conv();
    }

}
