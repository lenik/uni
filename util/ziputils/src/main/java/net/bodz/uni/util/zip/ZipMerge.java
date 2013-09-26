package net.bodz.uni.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.bodz.bas.io.res.builtin.InputStreamSource;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BatchCLI;
import net.bodz.bas.program.skel.FileHandler;
import net.bodz.bas.t.iterator.Iterables;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.impl.pojf.PojfFile;

/**
 * Merge Zip Archieves
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class ZipMerge
        extends BatchCLI {

    static final Logger logger = LoggerFactory.getLogger(ZipMerge.class);

    /**
     * @option -o =DEST-ZIP
     */
    File output;

    ZipOutputStream zout;

    @Override
    protected void reconfigure()
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
    public void processFile(FileHandler handler)
            throws Exception {
        IFile file = handler.getInputFile();

        if (!(file instanceof PojfFile))
            logger.warn("Not a local file: " + file);

        File _file = ((PojfFile) file).getInternalFile();

        logger.mesg(tr._("add "), _file);
        ZipFile zipFile = new ZipFile(_file);
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
                for (byte[] block : entrySource.to(StreamReading.class).blocks()) {
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
