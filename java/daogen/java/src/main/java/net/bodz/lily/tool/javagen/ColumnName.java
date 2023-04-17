package net.bodz.lily.tool.javagen;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;

public class ColumnName {

    public String column;
    public String columnQuoted;

    public String field;
    public String property;
    public String Property;

    public String keyProperty;
    public String refProperty;

    public String constField;

    public void setPropertyFromField() {
        this.property = this.field;
        this.Property = Strings.ucfirst(this.field);

        String property_name = StringId.UL.breakCamel(this.property);
        this.constField = property_name.toUpperCase();
    }

    @Override
    public String toString() {
        return column + "/" + field;
    }

}
