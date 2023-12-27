package net.bodz.bas.fmt.excel.db;

import java.util.Map;

public interface IHeaderLookup {

    Integer lookupHeader(Map<String, Integer> headers, String columnName);

}
