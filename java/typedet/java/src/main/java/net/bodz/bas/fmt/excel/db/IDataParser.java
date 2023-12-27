package net.bodz.bas.fmt.excel.db;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.t.catalog.IColumnMetadata;

public interface IDataParser {

    Object parse(IColumnMetadata column, String str)
            throws ParseException;

}
