package net.bodz.lily.tool.daogen.util;

import java.util.StringTokenizer;

import net.bodz.bas.db.sql.dialect.ISqlDialect;
import net.bodz.bas.db.sql.dialect.SqlDialects;
import net.bodz.bas.t.catalog.TableOid;

public class DialectFn {

    static ISqlDialect dialect = SqlDialects.POSTGRESQL;

    public static String quoteName(String name) {
        return dialect.qNameSmart(name);
    }

    public static String quoteQName(String qName) {
        StringTokenizer tokens = new StringTokenizer(qName, ".");
        StringBuilder sb = new StringBuilder(qName.length() * 2);
        int i = 0;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (i++ != 0)
                sb.append(".");
            String quoted = quoteName(token);
            sb.append(quoted);
        }
        return sb.toString();
    }

    public static String quoteQName(TableOid id) {
        StringBuilder sb = new StringBuilder(40);
        String catalogName = id.getCatalogName();
        String schemaName = id.getSchemaName();
        String tableName = id.getTableName();
        if (catalogName != null) {
            sb.append(quoteName(catalogName));
            sb.append('.');
        }
        if (schemaName != null) {
            sb.append(quoteName(schemaName));
            sb.append('.');
        }
        sb.append(quoteName(tableName));
        return sb.toString();
    }

}
