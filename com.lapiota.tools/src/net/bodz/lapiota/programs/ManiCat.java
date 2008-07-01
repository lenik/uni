package net.bodz.lapiota.programs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;
import net.bodz.bas.io.Files;

@Doc("Dump manifest in various files")
@Version( { 0, 0 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class ManiCat extends BasicCLI {

    static String MANIFEST = "META-INF/MANIFEST.MF";
    static String CHARSET  = "utf-8";

    void addDirectory(File file) throws IOException {
        File mf = new File(file, MANIFEST);
        if (!mf.exists())
            // info: dir have no manifest
            return;
        String data = Files.readAll(mf, CHARSET);
        printManifest("DIR", file, data);
    }

    void addJar(File file) throws IOException {
        JarFile jar = new JarFile(file);
        ZipEntry entry = jar.getEntry(MANIFEST);
        if (entry == null)
            // info: jar have no manifest
            return;
        InputStream in = jar.getInputStream(entry);
        String data = Files.readAll(in, CHARSET);
        String type = Files.getExtension(file, false).toUpperCase();
        printManifest(type, file, data);
    }

    static String SEPARATOR = ";======================================================================";

    void printManifest(String type, File file, String manifestData) {
        System.out.println(SEPARATOR);
        System.out.println(";== " + type + ": " + file);
        System.out.println(SEPARATOR);
        System.out.println(manifestData);
        System.out.println();
    }

    @Override
    protected void _main(File file) throws Throwable {
        if (file == null)
            _help();
        if (!file.exists()) {
            System.err.println("File not exists: " + file);
            return;
        }

        String ext = Files.getExtension(file).toLowerCase();
        if (file.isDirectory())
            addDirectory(file);
        else if (".jar".equals(ext))
            addJar(file);
        else if (".war".equals(ext))
            addJar(file);
        else if (".ear".equals(ext))
            addJar(file);
        else if (".zip".equals(ext))
            addJar(file);
        else
            System.err.println("unknown file type: " + file);
    }

    public static void main(String[] args) throws Throwable {
        new ManiCat().climain(args);
    }

}
