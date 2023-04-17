package net.bodz.lily.tool.javagen.reflect;

public class EntityFieldModel {

    public Class<?> fieldType;
    public String fieldName;
    public String fieldName_sqlOverride;
    public String description; // from xjdoc

    public String columnName; // sql escaped or not?

    public MaskFieldModel mask;

    public String getFieldNameForSql() {
        return fieldName_sqlOverride != null ? fieldName_sqlOverride : fieldName;
    }

}
