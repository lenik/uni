package net.bodz.lily.tool.daogen;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;

public class ColumnNaming {

    public String column;
    public String columnQuoted;

    public String fieldName;
    public String propertyName;
    public String capPropertyName;

    public String constFieldName;

    // public void setColumn(String column) {
    // this.column = column;
    // String camelName = StringId.UL.toCamel(column);
    // setProperty(camelName);
    // }

    public void initByFieldName(String fieldName) {
        initByPropertyName(fieldName);
    }

    static boolean lowerUpper(String property) {
        if (property.length() < 2)
            return false;
        return Character.isLowerCase(property.charAt(0)) //
                && Character.isUpperCase(property.charAt(1));
    }

    public static String capitalize(String propertyName) {
        if (lowerUpper(propertyName))
            return propertyName;
        else
            return Strings.ucfirst(propertyName);
    }

    public void initByPropertyName(String propertyName) {
        this.fieldName = propertyName;

        this.propertyName = propertyName;
        this.capPropertyName = capitalize(propertyName);
        String under_line = StringId.UL.breakCamel(this.propertyName);
        this.constFieldName = under_line.toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("column %s, field %s", //
                column, fieldName);
    }

}
