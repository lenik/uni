package net.bodz.lapiota.datafiles;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.IllegalUsageError;
import net.bodz.bas.types.util.Objects;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("Patch using the modified grep result")
@Version( { 0, 0 })
@RcsKeywords(id = "$Id: GrepPatch.java 0 2008-10-14 下午07:03:18 Shecti $")
public class GrepPatch extends BatchProcessCLI {

    @Option(doc = "enable #comment in grep file")
    boolean comment;

    @Override
    protected void _boot() throws Throwable {
        if (!inputEncoding.equals(outputEncoding))
            throw new IllegalUsageError(
                    "input and output encoding should be same");
    }

    @Override
    protected void doFileArgument(File file) throws Throwable {
        L.i.sig("[patch] ", file);
        int grepl = 0;

        String currentFileName = null;
        File target = null;
        List<String> loaded = null;

        for (String line : Files.readByLine2(inputEncoding, file)) {
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
                L.e.P("invalid grep format: no filename at ", filepos);
                continue;
            }
            String fileName = line.substring(0, col);
            line = line.substring(col + 1);

            col = line.indexOf(':');
            if (col == -1) {
                L.e.P("invalid grep format: no line number at ", filepos);
                continue;
            }
            String lineno = line.substring(0, col);
            line = line.substring(col + 1);
            int lno;
            try {
                lno = Integer.parseInt(lineno);
            } catch (NumberFormatException e) {
                L.e.P("illegal line number '", lineno, "' at ", filepos);
                continue;
            }
            if (lno < 1) {
                L.e.P("line number < 0 at ", filepos);
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
                L.e.P("line number ", lno, " out of bounds, at ", filepos);
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

            ProcessResult result = ProcessResult.compareAndSave();
            addResult(target, target, editTmp, result);
        } finally {
            editTmp.delete();
        }
    }

    public static void main(String[] args) throws Throwable {
        new GrepPatch().run(args);
    }

}
