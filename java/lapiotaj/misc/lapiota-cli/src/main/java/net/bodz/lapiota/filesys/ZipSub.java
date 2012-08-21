package net.bodz.lapiota.filesys;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

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
            L.status(CLINLS.getString("ZipSub.subtractFrom"), args[i]);
            ZipFile sub = new ZipFile(args[i]);
            int n = 0;
            for (ZipEntry entry : Iterables.otp(sub.entries())) {
                String name = entry.getName();
                if (removeSet.add(name))
                    n++;
            }
            L.info(CLINLS.getString("ZipSub.subtractFrom"), args[i], " (", n, CLINLS.getString("ZipSub.uniqEntries"));
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
                L.mesg(CLINLS.getString("ZipSub.removed"), name);
                removed++;
            } else {
                if (removed == 0) {
                    pending++;
                    continue;
                }
                if (tempOut == null)
                    tempOut = dumpHead(temp, destZip, pending);
                writeEntry(CLINLS.getString("ZipSub.write"), tempOut, destZip, entry);
            }
        }
        if (removed != 0 && tempOut == null)
            tempOut = dumpHead(temp, destZip, pending);

        L.status(CLINLS.getString("ZipSub.total") + removed + CLINLS.getString("ZipSub.entriesRemoved"));

        destZip.close();
        if (tempOut != null) {
            tempOut.close();
            L.mesg(CLINLS.getString("ZipSub.backup"), dest);
            File destBak = new File(destDir, dest.getName() + ".bak");
            if (destBak.exists()) {
                if (!destBak.delete())
                    throw new IOException(CLINLS.getString("ZipSub.cantDelete") + destBak);
            }
            if (!dest.renameTo(destBak))
                throw new IOException(CLINLS.getString("ZipSub.cantRename") + dest + CLINLS.getString("ZipSub._to_")
                        + destBak);
            if (!temp.renameTo(dest))
                throw new IOException(CLINLS.getString("ZipSub.cantRename") + temp + CLINLS.getString("ZipSub._to_")
                        + dest);
        } else {
            L.mesg(CLINLS.getString("ZipSub.noneRemoved"));
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
            writeEntry(CLINLS.getString("ZipSub.copy"), out, zip, headEntry);
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
                    L.status(title, name, " ", written, "/", size, //
                            " (", percent, "%)");
                    lastPercent = percent;
                }
            }
            in.close();
            // out.flush(); // ??
            out.closeEntry();
        }
        L.info(title, name, " ", entry.getCompressedSize(), "/", size + ". ");
    }

    @Override
    protected String _helpRestSyntax() {
        return CLINLS.getString("ZipSub.restSyntax");
    }

    public static void main(String[] args)
            throws Exception {
        new ZipSub().execute(args);
    }

}
