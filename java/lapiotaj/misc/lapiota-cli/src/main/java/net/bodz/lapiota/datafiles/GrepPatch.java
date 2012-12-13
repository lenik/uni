package net.bodz.lapiota.datafiles;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIAccessor;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.cli.skel.FileHandler;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
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
        inputEncoding = CLIAccessor.getInputEncoding(GrepPatch.this);
        outputEncoding = CLIAccessor.getOutputEncoding(GrepPatch.this);
        if (!inputEncoding.equals(outputEncoding))
            throw new IllegalUsageException(tr._("input and output encoding should be same"));
    }

    @Override
    protected void processImpl(FileHandler handler)
            throws Exception {
        IFile file = handler.getFile();

        logger.status(tr._("[patch] "), file);
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
                logger.error(tr._("invalid grep format: no filename at "), filepos);
                continue;
            }
            String fileName = line.substring(0, col);
            line = line.substring(col + 1);

            col = line.indexOf(':');
            if (col == -1) {
                logger.error(tr._("invalid grep format: no line number at "), filepos);
                continue;
            }
            String lineno = line.substring(0, col);
            line = line.substring(col + 1);
            int lno;
            try {
                lno = Integer.parseInt(lineno);
            } catch (NumberFormatException e) {
                logger.error(tr._("illegal line number \'"), lineno, "' at ", filepos);
                continue;
            }
            if (lno < 1) {
                logger.error("line number < 0 at ", filepos);
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
                logger.error("line number ", lno, " out of bounds, at ", filepos);
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
