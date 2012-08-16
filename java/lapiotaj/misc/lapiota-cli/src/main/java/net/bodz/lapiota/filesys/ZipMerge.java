package net.bodz.lapiota.filesys;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.bodz.bas.cli.skel.BatchCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.util.iter.Iterables;
import net.bodz.bas.vfs.IFile;

/**
 * Merge Zip Archieves
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
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
    protected void doFile(IFile file)
            throws IOException {
        L.mesg(CLINLS.getString("ZipMerge.add"), file);
        ZipFile zip = new ZipFile(file);
        try {
            for (ZipEntry s : Iterables.otp(zip.entries())) {
                InputStream ein = zip.getInputStream(s);
                ZipEntry t = new ZipEntry(s.getName());
                t.setComment(s.getComment());
                t.setCrc(s.getCrc());
                t.setExtra(s.getExtra());
                t.setMethod(s.getMethod());
                t.setSize(s.getSize());
                t.setTime((s.getTime()));
                zout.putNextEntry(t);

                String title = CLINLS.getString("ZipMerge.__add") + t.getName() + //
                        " (" + s.getSize() + CLINLS.getString("ZipMerge.bytes");
                int lastPercent = 0;
                long written = 0;
                for (byte[] block : Files.readByBlock(ein)) {
                    zout.write(block);
                    written += block.length;
                    int percent = (int) (100 * written / s.getSize());
                    if (percent != lastPercent) {
                        L.status(title, percent, "%)");
                        lastPercent = percent;
                    }
                }
                L.info();
            }
        } finally {
            zip.close();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new ZipMerge().execute(args);
    }

}
