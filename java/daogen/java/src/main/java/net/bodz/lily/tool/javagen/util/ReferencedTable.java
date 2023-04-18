package net.bodz.lily.tool.javagen.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;

public class ReferencedTable {

    ITableMetadata parentTable;
    Map<String, List<String>> columnToForeignAliases = new HashMap<>();

    public ReferencedTable(ITableMetadata parentTable) {
        if (parentTable == null)
            throw new NullPointerException("parentTable");
        this.parentTable = parentTable;
    }

    public ITableMetadata getParentTable() {
        return parentTable;
    }

    List<String> getOrCreate(String parentColumn) {
        List<String> set = columnToForeignAliases.get(parentColumn);
        if (set == null) {
            set = new ArrayList<>();
            columnToForeignAliases.put(parentColumn, set);
        }
        return set;
    }

    public void add(String parentColumn, String foreignColumn) {
        getOrCreate(parentColumn).add(foreignColumn);
    }

    public boolean isPrimaryKeyColumnsSet() {
        return getPrimaryKeyColumnsSetCount() > 0;
    }

    public int getPrimaryKeyColumnsSetCount() {
        int minSize = -1;
        for (String k : parentTable.getPrimaryKey().getColumnNames()) {
            List<String> set = columnToForeignAliases.get(k);
            if (set == null || set.isEmpty())
                return 0;
            if (minSize == -1 || set.size() < minSize)
                minSize = set.size();
        }
        if (minSize == -1)
            minSize = 0;
        return minSize;
    }

    public String[] getParentColumnNames() {
        return parentTable.getPrimaryKey().getColumnNames();
    }

    public IColumnMetadata[] getParentColumns() {
        return parentTable.getPrimaryKeyColumns();
    }

    public List<String[]> getForeignColumnNames() {
        int n = getPrimaryKeyColumnsSetCount();
        List<String[]> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String[] v = getForeignColumnNames(i);
            list.add(v);
        }
        return list;
    }

    public String[] getForeignColumnNames(int index) {
        String[] kv = parentTable.getPrimaryKey().getColumnNames();
        String[] av = new String[kv.length];
        for (int i = 0; i < kv.length; i++) {
            List<String> aliases = columnToForeignAliases.get(kv[i]);
            if (index >= aliases.size())
                return null;
            av[i] = aliases.get(index);
        }
        return av;
    }

    public IColumnMetadata[] getForeignColumns(ITableMetadata table, int index) {
        String[] names = getForeignColumnNames(index);
        IColumnMetadata[] columns = new IColumnMetadata[names.length];
        for (int i = 0; i < names.length; i++)
            columns[i] = table.getColumn(names[i]);
        return columns;
    }

    public List<CrossReference> buildForeignKeys(ITableMetadata foreignTable) {
        int n = getPrimaryKeyColumnsSetCount();
        List<CrossReference> fkList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            IColumnMetadata[] foreignColumns = this.getForeignColumns(foreignTable, i);
            CrossReference fk = new CrossReference();
            fk.manyToOne(foreignTable, foreignColumns, //
                    this.getParentTable(), this.getParentColumns());

            StringBuilder cn = new StringBuilder();
            cn.append("__fake_fk");
            for (IColumnMetadata column : foreignColumns)
                cn.append("_" + column.getName());
            fk.setConstraintName(cn.toString());
            fkList.add(fk);
        }
        return fkList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(parentTable.getId());
        sb.append("(" + StringArray.join(", ", getParentColumnNames()) + ")");
        sb.append(": ");
        int i = 0;
        for (String[] foreignColumnNames : getForeignColumnNames()) {
            if (i++ != 0)
                sb.append(", ");
            sb.append("(" + StringArray.join(", ", foreignColumnNames) + ")");
        }
        return sb.toString();
    }

}
