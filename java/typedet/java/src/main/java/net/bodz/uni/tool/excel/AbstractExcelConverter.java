package net.bodz.uni.tool.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.filetype.excel.ExcelParseOptions;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.adapter.WriterPrintOut;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.catalog.poi.SheetBook;
import net.bodz.bas.t.catalog.poi.SheetTable;

public abstract class AbstractExcelConverter
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(AbstractExcelConverter.class);

    private String destExtension;

    /**
     * Specify the output directory. Same dir default.
     *
     * @option -O --output-directory
     */
    File outDir;

    /**
     * Split each worksheet to separate files. (default)
     *
     * @option -s
     */
    boolean splitSheets;

    /**
     * Concatenate all sheets together.
     *
     * @option -w
     */
    boolean entireWorkbook;

    /**
     * Specify the text file encoding. (default utf-8)
     *
     * @option -e =CHARSET
     */
    String encoding = "utf-8";

    /**
     * Use sheet name as the entire file name, don't append.
     *
     * @option -N
     */
    boolean sheetNameOnly;

    /**
     * Output to stdout.
     *
     * @option -c
     */
    boolean stdout;

    public AbstractExcelConverter(String destExt) {
        this.destExtension = destExt;
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {

        if (splitSheets)
            entireWorkbook = false;
        if (entireWorkbook)
            splitSheets = false;
        if (!(splitSheets || entireWorkbook))
            splitSheets = true;

        for (String name : args) {
            File file = new File(name);
            if (!file.canRead()) {
                logger.error("Can't read from " + file);
                continue;
            }

            Workbook src = WorkbookFactory.create(file);
            SheetBook workbook = new SheetBook();
            ExcelParseOptions options = new ExcelParseOptions();
            workbook.readObject(src, options);

            File destFile = FilePath.dotExtension(file, destExtension);
            if (outDir != null)
                destFile = FilePath.dirName(destFile, outDir);

            convertWorkbook(workbook, destFile);
        }
    }

    protected void convertWorkbook(SheetBook workbook, File file)
            throws Exception {

        IPrintOut out = stdout ? Stdio.cout : null;

        for (SheetTable sheet : workbook.getSheets()) {
            String sheetName = sheet.getName();

            if (splitSheets) {
                File sheetFile = file;
                if (sheetNameOnly)
                    sheetFile = FilePath.baseName(file, sheetName + destExtension);
                else
                    sheetFile = FilePath.dotExtension(file, "-" + sheetName + destExtension);
                convertSheet(sheet, sheetFile);
            } else {
                if (out == null) {
                    FileOutputStream fos = new FileOutputStream(file);
                    OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);
                    out = new WriterPrintOut(writer);
                }

                logger.logf("Convert worksheet %s/%s to %s (append).", //
                        file.getName(), sheetName, file.getName());
                convertSheet(sheet, out);
            }
        } // for sheet
        if (out != null) {
            // out.flush();
            out.close();
        }
    }

    protected void convertSheet(SheetTable sheet, File file)
            throws Exception {
        String sheetName = sheet.getName();

        logger.logf("Convert worksheet %s/%s to %s.", //
                file.getName(), sheetName, file.getName());

        if (stdout) {
            convertSheet(sheet, Stdio.cout);
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);
            IPrintOut out = new WriterPrintOut(writer);

            convertSheet(sheet, out);

            out.flush();
        }
    }

    protected abstract void convertSheet(SheetTable sheet, IPrintOut out)
            throws Exception;

}
