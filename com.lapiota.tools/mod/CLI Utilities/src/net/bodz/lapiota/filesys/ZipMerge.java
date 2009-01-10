package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.Files;
import net.bodz.bas.types.util.Iterates;
import net.bodz.lapiota.wrappers.BatchCLI;

@Doc("Merge Zip Archieves")
@RcsKeywords(id = "$Id: ZipMerge.java 0 2008-12-12 下午04:13:33 Shecti $")
@Version( { 0, 0 })
public class ZipMerge extends BatchCLI {

    @Option(alias = "o", vnam = "DEST-ZIP")
    File            output;

    ZipOutputStream zout;

    @Override
    protected void _boot() throws IOException {
        OutputStream out = System.out;
        if (output != null)
            out = new FileOutputStream(output);
        zout = new ZipOutputStream(out);

        // zout.setComment(comment);
        // zout.setLevel(level);
        // zout.setMethod(method);
    }

    @Override
    protected void doFile(File file) throws IOException {
        L.m.P("add ", file);
        ZipFile zip = new ZipFile(file);
        try {
            for (ZipEntry s : Iterates.iterate(zip.entries())) {
                InputStream ein = zip.getInputStream(s);
                ZipEntry t = new ZipEntry(s.getName());
                t.setComment(s.getComment());
                t.setCrc(s.getCrc());
                t.setExtra(s.getExtra());
                t.setMethod(s.getMethod());
                t.setSize(s.getSize());
                t.setTime((s.getTime()));
                zout.putNextEntry(t);

                String title = "  add " + t.getName() + //
                        " (" + s.getSize() + " bytes, ";
                int lastPercent = 0;
                long written = 0;
                for (byte[] block : Files.readByBlock(ein)) {
                    zout.write(block);
                    written += block.length;
                    int percent = (int) (100 * written / s.getSize());
                    if (percent != lastPercent) {
                        L.i.sig(title, percent, "%)");
                        lastPercent = percent;
                    }
                }
                L.d.P();
            }
        } finally {
            zip.close();
        }
    }

    public static void main(String[] args) throws Throwable {
        new ZipMerge().run(args);
    }

}
