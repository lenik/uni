package net.bodz.lily.tool.daogen;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;

public class ColumnName {

    public String column;
    public String columnQuoted;

    public String field;
    public String property;
    public String Property;

//    public String keyProperty;
//    public String refProperty;

    public String constField;

    // public void setColumn(String column) {
    // this.column = column;
    // String camelName = StringId.UL.toCamel(column);
    // setProperty(camelName);
    // }

    public void setField(String field) {
        setProperty(field);
    }

    public void setProperty(String property) {
        this.field = property;
        this.property = property;
        this.Property = Strings.ucfirst(property);
        String under_line = StringId.UL.breakCamel(this.property);
        this.constField = under_line.toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("column %s, field %s", //
                column, field);
    }

}
