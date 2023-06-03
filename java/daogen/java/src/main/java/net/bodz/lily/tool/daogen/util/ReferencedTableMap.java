package net.bodz.lily.tool.daogen.util;

import java.util.HashMap;

import net.bodz.bas.t.catalog.ITableMetadata;

public class ReferencedTableMap
        extends HashMap<ITableMetadata, ReferencedTable> {

    private static final long serialVersionUID = 1L;

    ITableMetadata foreignTable;

    public ReferencedTableMap(ITableMetadata foreignTable) {
        this.foreignTable = foreignTable;
    }

    public ReferencedTable resolveTable(ITableMetadata parentTableKey) {
        ReferencedTable sub = get(parentTableKey);
        if (sub == null) {
            sub = new ReferencedTable(parentTableKey);
            put(parentTableKey, sub);
        }
        return sub;
    }

    public void dump() {
        System.out.println("on foreign talbe: " + foreignTable.getId());
        for (ReferencedTable ref : values()) {
            System.out.println("    ref " + ref);
        }
    }

}
