package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.io.resource.builtin.InputStreamSource;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.util.iter.Iterables;
import net.bodz.bas.vfs.CurrentDirectoryColo;

/**
 * Remove entries in zip file, which appears in another zips
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class ZipSub
        extends BasicCLI {

    /**
     * Remove all prefixes
     *
     * @option -p
     */
    boolean prefixMatch;

    /**
     * Remove all suffixes
     *
     * @option -s
     */
    boolean suffixMatch;

    @Override
    protected void doMain(String[] args)
            throws Exception {
        if (args.length < 2)
            _help();

        Set<String> removeSet = new HashSet<String>();
        for (int i = 1; i < args.length; i++) {
            logger.status(tr._("subtract from: "), args[i]);
            ZipFile sub = new ZipFile(args[i]);
            int n = 0;
            for (ZipEntry entry : Iterables.otp(sub.entries())) {
                String name = entry.getName();
                if (removeSet.add(name))
                    n++;
            }
            logger.info(tr._("subtract from: "), args[i], " (", n, tr._(" uniq entries)"));
        }

        File dest = CurrentDirectoryColo.getInstance().join(args[0]);
        String destName = FilePath.getBaseName(dest.getPath());
        File destDir = dest.getParentFile();

        ZipFile destZip = new ZipFile(dest);
        File temp = File.createTempFile(destName + "_", ".tmp", destDir);

        ZipOutputStream tempOut = null;
        int pending = 0;
        int removed = 0;

        for (ZipEntry entry : Iterables.otp(destZip.entries())) {
            String name = entry.getName();
            if (removeSet.contains(name)) {
                logger.mesg(tr._("removed "), name);
                removed++;
            } else {
                if (removed == 0) {
                    pending++;
                    continue;
                }
                if (tempOut == null)
                    tempOut = dumpHead(temp, destZip, pending);
                writeEntry(tr._("write "), tempOut, destZip, entry);
            }
        }
        if (removed != 0 && tempOut == null)
            tempOut = dumpHead(temp, destZip, pending);

        logger.status(tr._("Total ") + removed + tr._(" entries have been removed."));

        destZip.close();
        if (tempOut != null) {
            tempOut.close();
            logger.mesg(tr._("backup "), dest);
            File destBak = new File(destDir, dest.getName() + ".bak");
            if (destBak.exists()) {
                if (!destBak.delete())
                    throw new IOException(tr._("Can\'t delete ") + destBak);
            }
            if (!dest.renameTo(destBak))
                throw new IOException(tr._("Can\'t rename ") + dest + tr._(" to ")
                        + destBak);
            if (!temp.renameTo(dest))
                throw new IOException(tr._("Can\'t rename ") + temp + tr._(" to ")
                        + dest);
        } else {
            logger.mesg(tr._("Nothing has been removed. "));
        }
    }

    ZipOutputStream dumpHead(File outFile, ZipFile zip, int n)
            throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));
        // tempOut.setLevel(level);
        // tempOut.setMethod(method);
        // tempOut.setComment(comment);
        Enumeration<? extends ZipEntry> head = zip.entries();
        for (int hi = 0; hi < n; hi++) {
            ZipEntry headEntry = head.nextElement();
            writeEntry(tr._("copy "), out, zip, headEntry);
        }
        return out;
    }

    void writeEntry(String title, ZipOutputStream out, ZipFile zip, ZipEntry entry)
            throws IOException {
        String name = entry.getName();
        long size = entry.getSize();
        // out.setLevel(level);
        // out.putNextEntry(entry);
        out.putNextEntry(new ZipEntry(name));
        // out.setMethod(entry.getMethod());
        out.setComment(entry.getComment());
        if (size != 0) {
            InputStream in = zip.getInputStream(entry);
            InputStreamSource source = new InputStreamSource(in);

            long written = 0;
            int lastPercent = 0;
            for (byte[] block : source.tooling()._for(StreamReading.class).byteBlocks()) {
                out.write(block);
                written += block.length;
                int percent = (int) (100 * written / size);
                if (percent != lastPercent) {
                    logger.status(title, name, " ", written, "/", size, //
                            " (", percent, "%)");
                    lastPercent = percent;
                }
            }
            in.close();
            // out.flush(); // ??
            out.closeEntry();
        }
        logger.info(title, name, " ", entry.getCompressedSize(), "/", size + ". ");
    }

    @Override
    protected String _helpRestSyntax() {
        return tr._("DEST-ZIP MINUS-ZIP-LIST");
    }

    public static void main(String[] args)
            throws Exception {
        new ZipSub().execute(args);
    }

}
