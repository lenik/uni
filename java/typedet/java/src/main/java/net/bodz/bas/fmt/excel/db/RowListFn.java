package net.bodz.bas.fmt.excel.db;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.CellType;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.excel.XCell;
import net.bodz.bas.fmt.excel.XRow;
import net.bodz.bas.fmt.excel.XWorksheet;
import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.IMutableRow;
import net.bodz.bas.t.catalog.RowList;

public class RowListFn {

    public static void readFromSheet(RowList rowList, XWorksheet sheet, IDataParser parser, IHeaderLookup lookup)
            throws ParseException {
        Map<String, Integer> headerMap = sheet.getTable().getRow(0).toMap(SortOrder.NONE);
        readFromSheet(rowList, sheet, parser, 1, (IColumnMetadata column) -> {
            return lookup.lookupHeader(headerMap, column.getName());
        });
    }

    /**
     * @param map
     *            Convert column to the sheet column index.
     */
    public static void readFromSheet(RowList rowList, XWorksheet sheet, IDataParser parser, int startRow,
            Function<IColumnMetadata, Integer> map)
            throws ParseException {
        int rowIndex = rowList.getRowCount();

        List<XRow> sheetRows = sheet.getTable().getRows();
        int sheetRowCount = sheet.getRowCount();
        for (int iRow = startRow; iRow < sheetRowCount; iRow++) {
            XRow sheetRow = sheetRows.get(iRow);

            // check if cells comleted.
            int nCell = sheetRow.getCellCount();
            // int nColumn = _this.getMetadata().getColumnCount();
            // if (nCell < nColumn)
            if (nCell == 0)
                return;

            if (nCell == 1) {
                CellType cell0Type = sheetRow.get(0).getType();
                if (cell0Type == CellType.BLANK)
                    return;
            }

            IMutableRow row = rowList.newRow();
            MutableRowFn.readObject(row, parser, (IColumnMetadata column) -> {
                Integer columnIndex = map.apply(column);
                if (columnIndex == null)
                    return null;

                if (columnIndex >= sheetRow.getCellCount())
                    return null;

                XCell cell = sheetRow.get(columnIndex);
                String text = cell.getText();
                // Object value = cell.getValue();
                return text;
            });
            rowList.addRow(row);
        }
    }

}
