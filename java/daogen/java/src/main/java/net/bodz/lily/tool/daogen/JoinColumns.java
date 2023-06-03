package net.bodz.lily.tool.daogen;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.config.CatalogConfig;
import net.bodz.lily.tool.daogen.config.KeyColumnSettings;

public class JoinColumns
        implements
            IColumnOrder {

    Map<String, CrossReference> aliasMap = new LinkedHashMap<>();
    Map<CrossReference, String> refAliasMap = new HashMap<>();
    Map<String, AliasedColumn> aliasColumns = new LinkedHashMap<>();

    Map<String, IColumnMetadata> foreignColumns = new LinkedHashMap<>();

    boolean ignoreCase;
    String[] defaultParentColumns = { "id", "label", "description", "image" };

//    public JoinColumns() {
//    }

    KeyColumnSettings settings;

    public JoinColumns(CatalogConfig config) {
        this(config.keyColumnSettings);
    }

    public JoinColumns(KeyColumnSettings settings) {
        this.settings = settings;
    }

    public void addTable(ITableMetadata foreignTable) {
        addAll(foreignTable.getForeignKeys().values());
    }

    public void addAll(Collection<CrossReference> refs) {
        addAll(refs, ignoreCase);
    }

    public void addAll(Collection<CrossReference> refs, boolean ignoreCase) {
        for (CrossReference ref : refs) {
            add(ref, ignoreCase);
        }
    }

    public void add(CrossReference ref) {
        add(ref, ignoreCase);
    }

    public void add(CrossReference ref, boolean ignoreCase) {
        add("", ref, ignoreCase);
    }

    public void add(String prefix, CrossReference ref) {
        add(prefix, ref, ignoreCase);
    }

    public void add(String prefix, CrossReference ref, boolean ignoreCase) {
        if (refAliasMap.containsKey(ref))
            throw new IllegalArgumentException("already added: " + ref);

        String preferredSqlAlias = settings.getPreferredSqlAlias(ref);
        String preferredParentSqlAlias = Nullables.concat(prefix, preferredSqlAlias);
        Integer seq = null;
        String parentAlias;
        while (aliasMap.containsKey(parentAlias = concat(preferredParentSqlAlias, seq)))
            if (seq == null)
                seq = 1;
            else
                seq++;
        aliasMap.put(parentAlias, ref);
        refAliasMap.put(ref, parentAlias);

        for (IColumnMetadata foreignColumn : ref.getForeignColumns()) {
            IColumnMetadata existing = foreignColumns.get(foreignColumn.getName());
            if (existing != null)
                throw new IllegalUsageException("foreign column is already used: " + existing);
            foreignColumns.put(foreignColumn.getName(), foreignColumn);
        }

        ITableMetadata parent = ref.getParentTable();
        for (IColumnMetadata column : parent.getColumns()) {
            String parentColumnAlias = parentAlias + "_" + column.getName();
            AliasedColumn ac = new AliasedColumn(parentAlias, parentColumnAlias, column);
            aliasColumns.put(parentColumnAlias, ac);
        }
    }

    static String concat(String a, Object b) {
        if (b == null)
            return a;
        else
            return a + b;
    }

    @Override
    public SeqByPass getOrder(IColumnMetadata column) {
        int pass;
        if (column.isPrimaryKey())
            pass = 1;
        else if (!foreignColumns.containsKey(column.getName()))
            pass = 2;
        else
            pass = 3;
        return new SeqByPass(pass, column.getOrdinal());
    }

}
