package net.bodz.uni.tool.excel;

import java.util.List;

import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.t.catalog.poi.SheetCell;
import net.bodz.bas.t.catalog.poi.SheetRow;
import net.bodz.bas.t.catalog.poi.SheetTable;

/**
 * Convert Excel sheet to CSV.
 */
@ProgramName("excel2csv")
public class Excel2CSV
        extends AbstractExcelConverter {

    /**
     * The delimitor between fields. default comma(,).
     *
     * @option -d =DELIM
     */
    String delim = ",";

    /**
     * Text form of null cell.
     *
     * @option -n =STR
     */
    String nullText = "\\N";

    /**
     * Some rows doesn't have enough columns.
     *
     * @option -m =STR
     */
    String missingCell = ""; // "\\M";

    /**
     * How to escape special chars. (values: C, CSV. default CSV)
     *
     * @option -l =LANG
     */
    EscapeMode escapeMode = EscapeMode.CSV;

    public Excel2CSV() {
        super(".csv");
    }

    @Override
    protected void convertSheet(SheetTable sheet, IPrintOut out)
            throws Exception {
        int maxColumnCount = sheet.computeColumnCount();
        List<? extends SheetRow> rows = sheet.getRows();
        for (SheetRow row : rows) {
            int cellCount = row.getCellCount();
            for (int col = 0; col < cellCount; col++) {
                if (col != 0)
                    out.print(delim);
                SheetCell cell = row.getCell(col);
                format(out, cell);
            }
            for (int col = cellCount; col < maxColumnCount; col++) {
                out.print(delim);
                out.print(missingCell);
            }
            out.println();
        }
    }

    void format(IPrintOut out, SheetCell cell) {
        String text = cell.getText();
        if (text == null) {
            out.print(nullText);
            return;
        }

        String esc = escapeMode.escape(text);
        boolean escaped = esc.length() != text.length();

        if (escaped)
            esc = "\"" + esc + "\"";

        out.print(esc);
    }

    public static void main(String[] args)
            throws Exception {
        new Excel2CSV().execute(args);
    }

}
