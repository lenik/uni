package net.bodz.lily.tool.daogen;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;

public class ColumnNaming {

    public String column;
    public String columnQuoted;

    public String fieldName;
    public String propertyName;
    public String ucfirstPropertyName;

    public String constFieldName;

    // public void setColumn(String column) {
    // this.column = column;
    // String camelName = StringId.UL.toCamel(column);
    // setProperty(camelName);
    // }

    public void initByFieldName(String fieldName) {
        initByPropertyName(fieldName);
    }

    public void initByPropertyName(String propertyName) {
        this.fieldName = propertyName;
        this.propertyName = propertyName;
        this.ucfirstPropertyName = Strings.ucfirst(propertyName);
        String under_line = StringId.UL.breakCamel(this.propertyName);
        this.constFieldName = under_line.toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("column %s, field %s", //
                column, fieldName);
    }

}
