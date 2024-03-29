package net.bodz.lily.tool.daogen;

public class TableName {

    public String tableName;
    public String tableNameQuoted;
    public String compactName;
    public String compactNameQuoted;
    public String fullName;
    public String fullNameQuoted;

    public String className;
    public String packageName;
    public String simpleClassName;

    @Override
    public String toString() {
        return String.format("table %s, class %s", tableName, className);
    }

}
