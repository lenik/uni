package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.bodz.bas.cli.skel.BatchCLI;
import net.bodz.bas.io.resource.builtin.InputStreamSource;
import net.bodz.bas.io.resource.tools.StreamReading;
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
        logger.mesg(tr._("add "), file);
        ZipFile zipFile = new ZipFile(file);
        try {
            for (ZipEntry entry : Iterables.otp(zipFile.entries())) {
                ZipEntry outEntry = new ZipEntry(entry.getName());
                outEntry.setComment(entry.getComment());
                outEntry.setCrc(entry.getCrc());
                outEntry.setExtra(entry.getExtra());
                outEntry.setMethod(entry.getMethod());
                outEntry.setSize(entry.getSize());
                outEntry.setTime((entry.getTime()));
                zout.putNextEntry(outEntry);

                InputStream entryIn = zipFile.getInputStream(entry);
                InputStreamSource entrySource = new InputStreamSource(entryIn);

                String title = tr._("  add ") + outEntry.getName() + //
                        " (" + entry.getSize() + tr._(" bytes, ");
                int lastPercent = 0;
                long written = 0;
                for (byte[] block : entrySource.tooling()._for(StreamReading.class).byteBlocks()) {
                    zout.write(block);
                    written += block.length;
                    int percent = (int) (100 * written / entry.getSize());
                    if (percent != lastPercent) {
                        logger.status(title, percent, "%)");
                        lastPercent = percent;
                    }
                }
                logger.info();
            }
        } finally {
            zipFile.close();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new ZipMerge().execute(args);
    }

}
