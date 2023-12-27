package net.bodz.uni.tool.excel;

import java.io.File;

import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.fmt.excel.XWorkbook;
import net.bodz.bas.fmt.excel.XWorksheet;
import net.bodz.bas.fmt.xml.XmlFn;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;

/**
 * Convert excel to xml format.
 */
@ProgramName("excel2xml")
public class Excel2Xml
        extends AbstractExcelConverter {

    static final Logger logger = LoggerFactory.getLogger(Excel2Xml.class);

    public Excel2Xml() {
        super(".xml");
    }

    @Override
    protected void convertWorkbook(XWorkbook workbook, File file)
            throws Exception {
        if (splitSheets) {
            super.convertWorkbook(workbook, file);
        } else {
            logger.logf("Convert workbook %s to %s.\n", //
                    file.getName(), file.getName());
            XmlFn.save(workbook, file);
        }
    }

    @Override
    protected void convertSheet(XWorksheet sheet, File file)
            throws Exception {
        XmlFn.save(sheet, file);
    }

    @Override
    protected void convertSheet(XWorksheet sheet, IPrintOut out)
            throws Exception {
        throw new NotImplementedException();
    }

    public static void main(String[] args)
            throws Exception {
        new Excel2Xml().execute(args);
    }

}
