package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.bodz.bas.cli.BatchCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.lapiota.nls.CLINLS;

/**
 * Merge Zip Archieves
 */
@RcsKeywords(id = "$Id$")
@Version({ 0, 0 })
public class ZipMerge
        extends BatchCLI {

    /**
     * @option -o =DEST-ZIP
     */
    File output;

    ZipOutputStream zout;

    @Override
    protected void _boot()
            throws IOException {
        OutputStream out = System.out;
        if (output != null)
            out = new FileOutputStream(output);
        zout = new ZipOutputStream(out);

        // zout.setComment(comment);
        // zout.setLevel(level);
        // zout.setMethod(method);
    }

    @Override
    protected void doFile(File file)
            throws IOException {
        L.mesg(CLINLS.getString("ZipMerge.add"), file); //$NON-NLS-1$
        ZipFile zip = new ZipFile(file);
        try {
            for (ZipEntry s : Iterates.once(zip.entries())) {
                InputStream ein = zip.getInputStream(s);
                ZipEntry t = new ZipEntry(s.getName());
                t.setComment(s.getComment());
                t.setCrc(s.getCrc());
                t.setExtra(s.getExtra());
                t.setMethod(s.getMethod());
                t.setSize(s.getSize());
                t.setTime((s.getTime()));
                zout.putNextEntry(t);

                String title = CLINLS.getString("ZipMerge.__add") + t.getName() + // //$NON-NLS-1$
                        " (" + s.getSize() + CLINLS.getString("ZipMerge.bytes"); //$NON-NLS-1$ //$NON-NLS-2$
                int lastPercent = 0;
                long written = 0;
                for (byte[] block : Files.readByBlock(ein)) {
                    zout.write(block);
                    written += block.length;
                    int percent = (int) (100 * written / s.getSize());
                    if (percent != lastPercent) {
                        L.tinfo(title, percent, "%)"); //$NON-NLS-1$
                        lastPercent = percent;
                    }
                }
                L.detail().p();
            }
        } finally {
            zip.close();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new ZipMerge().run(args);
    }

}
