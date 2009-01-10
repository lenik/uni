package net.bodz.lapiota.filesys;

import static net.bodz.bas.types.util.Iterates.iterate;

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

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CWD;
import net.bodz.bas.io.Files;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("Remove entries in zip file, which appears in another zips")
@RcsKeywords(id = "$Id: ZipSubtract.java 0 2008-12-11 上午11:02:54 Shecti $")
@Version( { 0, 0 })
public class ZipSub extends BasicCLI {

    @Option(alias = "p", doc = "remove all prefixes")
    boolean prefixMatch;

    @Option(alias = "s", doc = "remove all suffixes")
    boolean suffixMatch;

    @Override
    protected void doMain(String[] args) throws Throwable {
        if (args.length < 2)
            _help();

        Set<String> removeSet = new HashSet<String>();
        for (int i = 1; i < args.length; i++) {
            L.i.sig("subtract from: ", args[i]);
            ZipFile sub = new ZipFile(args[i]);
            int n = 0;
            for (ZipEntry entry : iterate(sub.entries())) {
                String name = entry.getName();
                if (removeSet.add(name))
                    n++;
            }
            L.i.P("subtract from: ", args[i], " (", n, " uniq entries)");
        }

        File dest = CWD.get(args[0]);
        String destName = Files.getName(dest);
        File destDir = dest.getParentFile();

        ZipFile destZip = new ZipFile(dest);
        File temp = File.createTempFile(destName + "_", ".tmp", destDir);

        ZipOutputStream tempOut = null;
        int pending = 0;
        int removed = 0;
        for (ZipEntry entry : iterate(destZip.entries())) {
            String name = entry.getName();
            if (removeSet.contains(name)) {
                L.m.P("removed ", name);
                removed++;
            } else {
                if (removed == 0) {
                    pending++;
                    continue;
                }
                if (tempOut == null)
                    tempOut = dumpHead(temp, destZip, pending);
                writeEntry("write ", tempOut, destZip, entry);
            }
        }
        if (removed != 0 && tempOut == null)
            tempOut = dumpHead(temp, destZip, pending);

        L.m.sig("Total " + removed + " entries have been removed.");

        destZip.close();
        if (tempOut != null) {
            tempOut.close();
            L.m.P("backup ", dest);
            File destBak = new File(destDir, dest.getName() + ".bak");
            if (destBak.exists()) {
                if (!destBak.delete())
                    throw new IOException("Can't delete " + destBak);
            }
            if (!dest.renameTo(destBak))
                throw new IOException("Can't rename " + dest + " to " + destBak);
            if (!temp.renameTo(dest))
                throw new IOException("Can't rename " + temp + " to " + dest);
        } else {
            L.m.P("Nothing has been removed. ");
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
            writeEntry("copy ", out, zip, headEntry);
        }
        return out;
    }

    void writeEntry(String title, ZipOutputStream out, ZipFile zip,
            ZipEntry entry) throws IOException {
        String name = entry.getName();
        long size = entry.getSize();
        // out.setLevel(level);
        // out.putNextEntry(entry);
        out.putNextEntry(new ZipEntry(name));
        // out.setMethod(entry.getMethod());
        out.setComment(entry.getComment());
        if (size != 0) {
            InputStream in = zip.getInputStream(entry);
            long written = 0;
            int lastPercent = 0;
            for (byte[] block : Files.readByBlock(in)) {
                out.write(block);
                written += block.length;
                int percent = (int) (100 * written / size);
                if (percent != lastPercent) {
                    L.i.sig(title, name, " ", written, "/", size, //
                            " (", percent, "%)");
                    lastPercent = percent;
                }
            }
            in.close();
            // out.flush(); // ??
            out.closeEntry();
        }
        L.i.P(title, name, " ", entry.getCompressedSize(), "/", size + ". ");
    }

    @Override
    protected String _helpRestSyntax() {
        return "DEST-ZIP MINUS-ZIP-LIST";
    }

    public static void main(String[] args) throws Throwable {
        new ZipSub().run(args);
    }

}
