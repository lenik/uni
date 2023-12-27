package net.bodz.uni.tool.excel;

import java.io.File;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import net.bodz.bas.fmt.excel.ExcelParseOptions;
import net.bodz.bas.fmt.excel.XWorkbook;
import net.bodz.bas.fmt.xml.XmlFn;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.impl.TreeOutImpl;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * Dump excel sheets in text format.
 */
public class ExcelDump
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(ExcelDump.class);

    /**
     * Dumper in xml format.
     *
     * @option -x --xml
     */
    boolean xmlFormat;

    @Override
    protected void mainImpl(String... args)
            throws Exception {

        ITreeOut out = TreeOutImpl.from(stdout);

        for (String name : args) {
            File file = new File(name);
            if (!file.canRead()) {
                logger.error("Can't read from " + file);
                continue;
            }

            out.println("File: " + file);

            Workbook src = WorkbookFactory.create(file);
            XWorkbook workbook = new XWorkbook();
            ExcelParseOptions options = new ExcelParseOptions();
            workbook.readObject(src, options);

            if (xmlFormat) {
                String xml = XmlFn.toString(workbook);
                System.out.println(xml);
                return;
            }

            workbook.dump(out);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new ExcelDump().execute(args);
    }

}
