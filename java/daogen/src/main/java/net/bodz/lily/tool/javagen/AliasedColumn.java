package net.bodz.lily.tool.javagen;

import net.bodz.bas.t.catalog.IColumnMetadata;

public class AliasedColumn {

    String tableAlias;
    String columnAlias;
    IColumnMetadata column;

    public AliasedColumn(String tableAlias, String columnAlias, IColumnMetadata column) {
        this.tableAlias = tableAlias;
        this.columnAlias = columnAlias;
        this.column = column;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public IColumnMetadata getColumn() {
        return column;
    }

    public void setColumn(IColumnMetadata column) {
        this.column = column;
    }

}
