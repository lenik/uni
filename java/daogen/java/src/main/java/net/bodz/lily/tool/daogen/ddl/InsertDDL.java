package net.bodz.lily.tool.daogen.ddl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.lily.tool.daogen.DialectFn;

public class InsertDDL {

    private String tableName;
    private Map<String, String> fieldMap;

    public InsertDDL(String tableName) {
        this.tableName = tableName;
        this.fieldMap = new LinkedHashMap<>();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, String> fieldMap) {
        if (fieldMap == null)
            throw new NullPointerException("fieldMap");
        this.fieldMap = fieldMap;
    }

    public void set(String fieldName, Number number) {
        put(fieldName, number.toString());
    }

    public void set(String fieldName, String str) {
        String qstr = DialectFn.quoteText(str);
        put(fieldName, qstr);
    }

    public void put(String fieldName, String sqlContent) {
        if (fieldName == null)
            throw new NullPointerException("fieldName");
        if (sqlContent == null)
            throw new NullPointerException("sqlContent");
        fieldMap.put(fieldName, sqlContent);
    }

    public void remove(String fieldName) {
        fieldMap.remove(fieldName);
    }

    @Override
    public String toString() {
        int n = fieldMap.size();
        StringBuilder sb = new StringBuilder(32 * n);
        StringBuilder tail = new StringBuilder(16 * n);
        sb.append("insert into " + tableName + "(");

        int i = 0;
        for (Entry<String, String> entry : fieldMap.entrySet()) {
            String fieldName = entry.getKey();
            String sqlContent = entry.getValue();
            if (i++ != 0) {
                sb.append(", ");
                tail.append(", ");
            }
            sb.append(fieldName);
            tail.append(sqlContent);
        }

        sb.append(") values(");
        sb.append(tail.toString());
        sb.append(")");
        return sb.toString();
    }

}
