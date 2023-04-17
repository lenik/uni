package net.bodz.lily.tool.javagen.util;

import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;

public class ReferencedTable {

    ITableMetadata parentTable;
    Map<String, String> columnToForeignAlias = new HashMap<>();

    public ReferencedTable(ITableMetadata parentTable) {
        if (parentTable == null)
            throw new NullPointerException("parentTable");
        this.parentTable = parentTable;
    }

    public ITableMetadata getParentTable() {
        return parentTable;
    }

    public void add(String parentColumn, String foreignColumn) {
        columnToForeignAlias.put(parentColumn, foreignColumn);
    }

    public boolean isPrimaryKeyColumnsSet() {
        for (String k : parentTable.getPrimaryKey().getColumnNames()) {
            if (!columnToForeignAlias.containsKey(k))
                return false;
        }
        return true;
    }

    public String[] getParentColumnNames() {
        return parentTable.getPrimaryKey().getColumnNames();
    }

    public IColumnMetadata[] getParentColumns() {
        return parentTable.getPrimaryKeyColumns();
    }

    public String[] getForeignColumnNames() {
        String[] kv = parentTable.getPrimaryKey().getColumnNames();
        String[] av = new String[kv.length];
        for (int i = 0; i < kv.length; i++) {
            String alias = columnToForeignAlias.get(kv[i]);
            av[i] = alias;
        }
        return av;
    }

    public IColumnMetadata[] getForeignColumns(ITableMetadata table) {
        String[] names = getForeignColumnNames();
        IColumnMetadata[] columns = new IColumnMetadata[names.length];
        for (int i = 0; i < names.length; i++)
            columns[i] = table.getColumn(names[i]);
        return columns;
    }

    public CrossReference buildForeignKey(ITableMetadata foreignTable) {
        IColumnMetadata[] foreignColumns = this.getForeignColumns(foreignTable);
        CrossReference xref = new CrossReference();
        xref.manyToOne(foreignTable, foreignColumns, //
                this.getParentTable(), this.getParentColumns());

        StringBuilder cn = new StringBuilder();
        cn.append("__fake_fk");
        for (IColumnMetadata column : foreignColumns)
            cn.append("_" + column.getName());
        xref.setConstraintName(cn.toString());

        return xref;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(parentTable.getId());
        sb.append("(" + StringArray.join(", ", getParentColumnNames()) + ")");
        sb.append(": ");
        sb.append("(" + StringArray.join(", ", getForeignColumnNames()) + ")");
        return sb.toString();
    }

}
