package net.bodz.lapiota.javatools;

import java.io.File;
import java.net.URL;

import net.bodz.bas.cli.util.SNMJava;
import net.bodz.bas.io.Files;
import net.bodz.lapiota.eclipse.jdt.J4conv;

public class GenerateLauncherTest {

    public void testJ4conv() throws Throwable {
        String tmpdir = System.getenv("TEMP");
        File outdir = new File(tmpdir, "testj4conv");
        try {
            outdir.mkdirs();

            URL srcurl = SNMJava.findSrc(J4conv.class);
            String srcfile = srcurl.getFile();
            int reslen = J4conv.class.getName().length() + ".java".length();
            String srcdir = srcfile.substring(0, srcfile.length() - reslen);

            // XXX must use a separate java.exe to get a clean class loader.
            GenerateLauncher gl = new GenerateLauncher();

            gl.run("-r", "-O", outdir.toString(), srcdir);

        } finally {
            Files.deleteTree(outdir);
        }
    }

}
