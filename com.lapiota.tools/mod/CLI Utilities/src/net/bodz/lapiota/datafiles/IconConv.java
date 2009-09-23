package net.bodz.lapiota.datafiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.Files;
import net.bodz.bas.types.HashTextMap;
import net.bodz.bas.types.TextMap;
import net.bodz.bas.types.util.Strings;
import net.bodz.bas.xml.XMLs;
import net.bodz.lapiota.wrappers.BatchCLI;
import net.bodz.swt.gui.SWTConfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@BootInfo(userlibs = { "bodz_swt" }, configs = SWTConfig.class)
@Doc("Icon files batch converter")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class IconConv extends BatchCLI {

    @Option(alias = "p", vnam = "DIR", doc = "Java project (eclipse) directory to save result files")
    File                    projectDir;
    List<File>              srcDirs;

    @Option(alias = "m", vnam = "FORMAT", doc = "Format string, args: NAME, W, H, DEPTH, EXT")
    String                  nameFormat  = "%s-%dx%dx%d.%s";

    @Option(alias = "t", vnam = "TYPE", doc = "convert to this format, default GIF")
    String                  imageType   = "gif";
    int                     imageFormat = SWT.IMAGE_GIF;

    private String          extension;

    static TextMap<Integer> formats;
    static TextMap<String>  extensions;
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
    protected void _boot() throws Exception {
        srcDirs = new ArrayList<File>();

        File classpathFile = new File(projectDir, ".classpath"); //$NON-NLS-1$
        if (!classpathFile.isFile())
            throw new FileNotFoundException(classpathFile.getPath());
        XMLs.parse(classpathFile, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String name,
                    Attributes attributes) throws SAXException {
                if (!"classpathentry".equals(name)) //$NON-NLS-1$
                    return;
                String kind = attributes.getValue("kind"); //$NON-NLS-1$
                if ("src".equals(kind)) {//$NON-NLS-1$
                    String srcPath = attributes.getValue("path"); //$NON-NLS-1$
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

    @Override
    protected void doFile(File file) throws Exception {
        String base = Strings.beforeLast(file.getName(), '.');
        if (base == null)
            base = file.getName();
        File outDir = file.getParentFile();
        if (projectDir != null) {
            outDir = findPackageDir(base); // on win32, case is ignored
            String typeName = Strings.afterLast(base, '.');
            if (typeName == null)
                typeName = base;
            File testFile = new File(outDir, typeName + ".java");
            testFile = Files.canoniOf(testFile);
            String caseFix = testFile.getName();
            caseFix = Strings.beforeLast(caseFix, '.');
            if (!base.equals(caseFix)) {
                L.debug("Case fixed: ", typeName, " -> ", caseFix);
                outDir = testFile.getParentFile();
                base = caseFix;
            }
        }

        FileInputStream in = new FileInputStream(file);
        ImageLoader loader = new ImageLoader();
        ImageData[] slices = loader.load(in);
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
            String outName = String.format(nameFormat, base, slice.width, slice.height,
                    slice.depth, extension);
            File outFile = new File(outDir, outName);
            L.mesg("Create ", outFile);
            ImageLoader saver = new ImageLoader();
            saver.data = new ImageData[] { slice };
            FileOutputStream out = new FileOutputStream(outFile);
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

    File findPackageDir(String typeName) throws FileNotFoundException {
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

    public static void main(String[] args) throws Exception {
        new IconConv().run(args);
    }

}
