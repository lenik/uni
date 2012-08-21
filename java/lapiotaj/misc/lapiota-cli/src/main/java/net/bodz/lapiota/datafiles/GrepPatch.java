package net.bodz.lapiota.datafiles;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.util.Nullables;
import net.bodz.bas.vfs.IFile;

/**
 * Patch using the modified grep result (grep -Hn)
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class GrepPatch
        extends BatchEditCLI {

    Charset inputEncoding;
    Charset outputEncoding;

    /**
     * Enable #comment in grep file
     *
     * @option
     */
    boolean comment;

    @Override
    protected void _boot()
            throws Exception {
        inputEncoding = parameters().getInputEncoding();
        outputEncoding = parameters().getOutputEncoding();
        if (!inputEncoding.equals(outputEncoding))
            throw new IllegalUsageException(CLINLS.getString("GrepPatch.diffInOut"));
    }

    @Override
    protected void doFileArgument(IFile file)
            throws Exception {
        L.status(CLINLS.getString("GrepPatch._patch"), file);
        int grepl = 0;

        String currentFileName = null;
        IFile target = null;
        List<String> loaded = null;

        for (String line : file.tooling()._for(StreamReading.class).lines()) {
            grepl++;
            String filepos = file + ":" + grepl;

            if (comment) {
                if (line.startsWith("#"))
                    continue;
            }
            if (line.isEmpty())
                continue;
            int col = line.indexOf(':');
            if (col == -1) {
                L.error(CLINLS.getString("GrepPatch.grepNoFilename"), filepos);
                continue;
            }
            String fileName = line.substring(0, col);
            line = line.substring(col + 1);

            col = line.indexOf(':');
            if (col == -1) {
                L.error(CLINLS.getString("GrepPatch.grepNoLineNum"), filepos);
                continue;
            }
            String lineno = line.substring(0, col);
            line = line.substring(col + 1);
            int lno;
            try {
                lno = Integer.parseInt(lineno);
            } catch (NumberFormatException e) {
                L.error(CLINLS.getString("GrepPatch.badlLineNum"), lineno, "' at ", filepos);
                continue;
            }
            if (lno < 1) {
                L.error("line number < 0 at ", filepos);
                continue;
            }

            if (!Nullables.equals(fileName, currentFileName)) {
                if (currentFileName != null)
                    save(target, loaded);
                currentFileName = fileName;
                target = getOutputFile(fileName, file.getParentFile());
                loaded = target.tooling()._for(StreamReading.class).listLines();
            }

            if (lno > loaded.size()) {
                L.error("line number ", lno, " out of bounds, at ", filepos);
                continue;
            }

            loaded.set(lno - 1, line); // throws IndexOutOfBoundsException
        }

        if (currentFileName != null)
            save(target, loaded);
    }

    void save(IFile target, List<String> lines)
            throws IOException {
        IFile editTmp = _getEditTmp(target);
        try {

            PrintStream out = editTmp.getOutputTarget(outputEncoding).newPrintStream();
            for (String line : lines)
                out.println(line);
            out.close();

            EditResult result = EditResult.compareAndSave();
            addResult(target, target, editTmp, result);
        } finally {
            editTmp.delete();
        }
    }

    public static void main(String[] args)
            throws Exception {
        new GrepPatch().execute(args);
    }

}
