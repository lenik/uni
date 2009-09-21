package net.bodz.lapiota.datafiles;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.IllegalUsageException;
import net.bodz.bas.types.util.Objects;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.wrappers.BatchEditCLI;

@Doc("Patch using the modified grep result (grep -Hn)")
@RcsKeywords(id = "$Id$")
@Version( { 0, 0 })
public class GrepPatch extends BatchEditCLI {

    Charset inputEncoding;
    Charset outputEncoding;

    @Option(doc = "enable #comment in grep file")
    boolean comment;

    @Override
    protected void _boot() throws Exception {
        inputEncoding = parameters().getInputEncoding();
        outputEncoding = parameters().getOutputEncoding();
        if (!inputEncoding.equals(outputEncoding))
            throw new IllegalUsageException(CLINLS.getString("GrepPatch.diffInOut")); //$NON-NLS-1$
    }

    @Override
    protected void doFileArgument(File file) throws Exception {
        L.tinfo(CLINLS.getString("GrepPatch._patch"), file); //$NON-NLS-1$
        int grepl = 0;

        String currentFileName = null;
        File target = null;
        List<String> loaded = null;

        for (String line : Files.readByLine2(inputEncoding, file)) {
            grepl++;
            String filepos = file + ":" + grepl; //$NON-NLS-1$

            if (comment) {
                if (line.startsWith("#")) //$NON-NLS-1$
                    continue;
            }
            if (line.isEmpty())
                continue;
            int col = line.indexOf(':');
            if (col == -1) {
                L.error(CLINLS.getString("GrepPatch.grepNoFilename"), filepos); //$NON-NLS-1$
                continue;
            }
            String fileName = line.substring(0, col);
            line = line.substring(col + 1);

            col = line.indexOf(':');
            if (col == -1) {
                L.error(CLINLS.getString("GrepPatch.grepNoLineNum"), filepos); //$NON-NLS-1$
                continue;
            }
            String lineno = line.substring(0, col);
            line = line.substring(col + 1);
            int lno;
            try {
                lno = Integer.parseInt(lineno);
            } catch (NumberFormatException e) {
                L.error(CLINLS.getString("GrepPatch.badlLineNum"), lineno, "' at ", filepos); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            if (lno < 1) {
                L.error("line number < 0 at ", filepos); //$NON-NLS-1$
                continue;
            }

            if (!Objects.equals(fileName, currentFileName)) {
                if (currentFileName != null)
                    save(target, loaded);
                currentFileName = fileName;
                target = getOutputFile(fileName, file.getParentFile());
                loaded = Files.readLines(target, inputEncoding);
            }

            if (lno > loaded.size()) {
                L.error("line number ", lno, " out of bounds, at ", filepos); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

            loaded.set(lno - 1, line); // throws IndexOutOfBoundsException
        }

        if (currentFileName != null)
            save(target, loaded);
    }

    void save(File target, List<String> lines) throws IOException {
        File editTmp = _getEditTmp(target);
        try {

            Writer out = Files.writeTo(editTmp, outputEncoding);
            for (String line : lines)
                out.write(line);
            out.close();

            EditResult result = EditResult.compareAndSave();
            addResult(target, target, editTmp, result);
        } finally {
            editTmp.delete();
        }
    }

    public static void main(String[] args) throws Exception {
        new GrepPatch().run(args);
    }

}
