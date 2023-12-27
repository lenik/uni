package net.bodz.bas.fmt.excel.db;

import java.util.function.Function;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.IMutableRow;
import net.bodz.bas.t.catalog.IRowSet;
import net.bodz.bas.t.catalog.IRowSetMetadata;

public class MutableRowFn {

    public static void readObject(IMutableRow row, IDataParser parser, Function<IColumnMetadata, String> map)
            throws ParseException {
        IRowSet rowSet = row.getRowSet();
        IRowSetMetadata metadata = rowSet.getMetadata();
        int cc = metadata.getColumnCount();
        for (int i = 0; i < cc; i++) {
            IColumnMetadata column = metadata.getColumn(i);
            String text = map.apply(column);
            if (text == null) {
                row.setCellData(i, null);
            } else {
                Object val = parser.parse(column, text);
                row.setCellData(i, val);
            }
        }
    }

}
