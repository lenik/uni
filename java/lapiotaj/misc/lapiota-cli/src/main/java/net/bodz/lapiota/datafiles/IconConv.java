package net.bodz.lapiota.datafiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.string.StringPart;
import net.bodz.bas.cli.skel.BatchCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.xml.XMLs;

/**
 * Icon files batch converter
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class IconConv
        extends BatchCLI {

    /**
     * Java project (eclipse) directory to save result files
     *
     * @option -p =DIR
     */
    File projectDir;
    List<File> srcDirs;

    /**
     * Format string, args: NAME, W, H, DEPTH, EXT
     *
     * @option -m =FORMAT
     */
    String nameFormat = "%s-%dx%dx%d.%s";

    /**
     * convert to this format, default GIF
     *
     * @option -t =TYPE
     */
    String imageType = "gif";
    int imageFormat = SWT.IMAGE_GIF;

    private String extension;

    static TextMap<Integer> formats;
    static TextMap<String> extensions;
    static {
        formats = new HashTextMap<Integer>();
        formats.put("BMP", SWT.IMAGE_BMP_RLE);
        formats.put("RLE", SWT.IMAGE_BMP_RLE);
        formats.put("OS2", SWT.IMAGE_OS2_BMP);
        formats.put("ICO", SWT.IMAGE_ICO);
        formats.put("JPG", SWT.IMAGE_JPEG);
        formats.put("JPEG", SWT.IMAGE_JPEG);
        formats.put("GIF", SWT.IMAGE_GIF);
        formats.put("PNG", SWT.IMAGE_PNG);
        formats.put("TIF", SWT.IMAGE_TIFF);
        formats.put("TIFF", SWT.IMAGE_TIFF);

        extensions = new HashTextMap<String>();
        extensions.put("BMP", "bmp");
        extensions.put("RLE", "bmp");
        extensions.put("OS2", "bmp");
        extensions.put("ICO", "ico");
        extensions.put("JPG", "jpg");
        extensions.put("JPEG", "jpg");
        extensions.put("GIF", "gif");
        extensions.put("PNG", "png");
        extensions.put("TIF", "tif");
        extensions.put("TIFF", "tif");
    }

    @Override
    protected void _boot()
            throws Exception {
        srcDirs = new ArrayList<File>();

        File classpathFile = new File(projectDir, ".classpath");
        if (!classpathFile.isFile())
            throw new FileNotFoundException(classpathFile.getPath().toString());

        XMLs.parse(classpathFile, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String name, Attributes attributes)
                    throws SAXException {
                if (!"classpathentry".equals(name))
                    return;
                String kind = attributes.getValue("kind");
                if ("src".equals(kind)) {
                    String srcPath = attributes.getValue("path");
                    if (srcPath != null)
                        srcDirs.add(new File(projectDir, srcPath));
                }
            }
        });

        imageType = imageType.toUpperCase();
        if (!formats.containsKey(imageType)) {
            throw new IllegalArgumentException("Bad image type: " + imageType);
        }
        imageFormat = formats.get(imageType);
        extension = extensions.get(imageType);
    }

    public void convertIconFile(File file)
            throws Exception {
        String base = StringPart.beforeLast(file.getName(), '.');
        if (base == null)
            base = file.getName();
        File outDir = file.getParentFile();
        if (projectDir != null) {
            outDir = findPackageDir(base); // on win32, case is ignored
            String typeName = StringPart.afterLast(base, '.');
            if (typeName == null)
                typeName = base;
            File testFile = new File(outDir, typeName + ".java");
            String caseFix = testFile.getName();
            caseFix = StringPart.beforeLast(caseFix, '.');
            if (!base.equals(caseFix)) {
                logger.debug("Case fixed: ", typeName, " -> ", caseFix);
                outDir = testFile.getParentFile();
                base = caseFix;
            }
        }

        ImageLoader loader = new ImageLoader();
        ImageData[] slices = loader.load(new FileInputStream(file));
        if (slices.length == 0)
            return;

        for (ImageData slice : slices) {
            switch (imageFormat) {
            case SWT.IMAGE_ICO:
                if (slice.width > 48 || slice.height > 48)
                    continue;
                break;
            case SWT.IMAGE_JPEG:
                if (slice.depth < 16)
                    continue;
                break;
            case SWT.IMAGE_GIF:
                if (slice.depth > 8)
                    continue;
                break;
            }
            String outName = String.format(nameFormat, base, slice.width, slice.height, slice.depth, extension);
            File outFile = new File(outDir, outName);
            logger.mesg("Create ", outFile);
            ImageLoader saver = new ImageLoader();
            saver.data = new ImageData[] { slice };

            OutputStream out = new FileOutputStream(outFile);
            try {
                saver.save(out, imageFormat);
                out.close();
            } catch (Exception e) {
                out.close();
                outFile.delete();
                throw e;
            }
        }
    }

    File findPackageDir(String typeName)
            throws FileNotFoundException {
        // int dot = typeName.lastIndexOf('.');
        // String packageName = dot == -1 ? null : typeName.substring(0, dot);
        String fileName = typeName.replace('.', '/') + ".java";
        for (File srcDir : srcDirs) {
            File f = new File(srcDir, fileName);
            if (f.exists()) {
                File packageDir = f.getParentFile();
                return packageDir;
            }
        }
        throw new FileNotFoundException("(*src-dirs*/)" + fileName);
    }

    public static void main(String[] args)
            throws Exception {
        new IconConv().execute(args);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (File file : expandPojfFiles(args))
            convertIconFile(file);
    }

}
