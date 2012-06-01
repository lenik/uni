package net.bodz.lapiota.filesys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.cli.BatchEditCLI;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.lapiota.nls.CLINLS;

/**
 * Resource packer
 */
@ProgramName("respack")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class ResourcePacker
        extends BatchEditCLI {

    /**
     * output jar file name
     *
     * @option -o
     */
    protected File outputFile;

    private JarOutputStream jarOut;

    private JarOutputStream getJarOut() {
        if (jarOut == null) {
            if (outputFile == null) {
                assert currentStartFile != null;
                outputFile = new File(currentStartFile.getPath() + ".jar"); //$NON-NLS-1$
            }
            try {
                OutputStream out = new FileOutputStream(outputFile);
                jarOut = new JarOutputStream(out);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        return jarOut;
    }

    static Pattern invalidChars;
    static {
        invalidChars = Pattern.compile("[^\\p{Alnum}_]", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
    }

    static interface NameConverter {
        String convert(String part, int index, int size);
    }

    /**
     * Convert each segment in the path separated by file.separator using converter.
     *
     * This don't care about relative or canonical form, what form passed in and that form pass out.
     */
    static File convertNameParts(File file, NameConverter converter) {
        List<String> parts = new ArrayList<String>();
        while (file != null) {
            String part = file.getName();
            parts.add(part);
            file = file.getParentFile();
        }
        int size = parts.size();
        for (int i = size - 1; i >= 0; i--) {
            String part = converter.convert(parts.get(i), size - 1 - i, size);
            if (file == null)
                file = new File(part);
            else
                file = new File(file, part);
        }
        return file;
    }

    int emptyIndex = 0;

    String convertNamePart(String part) {
        part = invalidChars.matcher(part).replaceAll(""); //$NON-NLS-1$
        if (part.isEmpty())
            part = "_" + ++emptyIndex; //$NON-NLS-1$
        if (Character.isDigit(part.charAt(0)))
            part = "_" + part; //$NON-NLS-1$
        return part;
    }

    /**
     * @param filename
     *            may be in relative form.
     */
    String resNameOf(String filename) {
        File vfile = new File(filename);
        vfile = convertNameParts(vfile, new NameConverter() {
            @Override
            public String convert(String part, int index, int size) {
                if (index < size - 1) {
                    part = convertNamePart(part);
                    return part.toLowerCase();
                } else {// base name
                    String ext = FilePath.getExtension(part, true);
                    String name = part.substring(0, part.length() - ext.length());
                    name = convertNamePart(name);
                    return name + ext;
                }
            }
        });
        return vfile.getPath();
    }

    @Override
    protected EditResult doEdit(File file)
            throws IOException {
        JarOutputStream out = getJarOut();
        String name = getRelativeName(file);
        String ename = resNameOf(name);
        L.tmesg(CLINLS.getString("ResourcePacker.add"), ename); //$NON-NLS-1$
        ZipEntry ze = new ZipEntry(ename);
        out.putNextEntry(ze);
        Files.copy(file, out);
        out.closeEntry();
        L.info(CLINLS.getString("ResourcePacker.add"), ename, " [", ze.getCompressedSize(), "/", ze.getSize(), "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return EditResult.pass();
    }

    @Override
    protected File _getEditTmp(File file)
            throws IOException {
        return null;
    }

    @Override
    protected void _exit()
            throws Exception {
        if (jarOut != null) {
            jarOut.finish();
            jarOut.close();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new ResourcePacker().run(args);
    }

}