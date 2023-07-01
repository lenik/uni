package net.bodz.lily.tool.daogen.config;

import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.err.DuplicatedKeyException;
import net.bodz.bas.t.catalog.ColumnOid;

public class ColumnRefMap {

    Map<String, ColumnOid> alias2QColumn = new HashMap<>();
//    Map<ColumnOid, Set<String>> qColumnAliases = new HashMap<>();

    public boolean isEmpty() {
        return alias2QColumn.isEmpty();
    }

    public void addColumnRef(String columnAlias, String qColumnName) {
        ColumnOid qColumn = ColumnOid.parse(qColumnName);
        addColumnRef(columnAlias, qColumn);
    }

    public void addColumnRef(String columnAlias, ColumnOid qColumn) {
        ColumnOid existing = alias2QColumn.get(columnAlias);
        if (existing != null)
            throw new DuplicatedKeyException(columnAlias, existing);

        alias2QColumn.put(columnAlias, qColumn);

//        Set<String> aliases = findAliases(qColumn);
//        aliases.add(columnAlias);
    }

//    public Set<String> findAliases(ColumnOid qColumn) {
//        Set<String> aliases = qColumnAliases.get(qColumn);
//        if (aliases == null)
//            qColumnAliases.put(qColumn, aliases = new LinkedHashSet<>());
//        return aliases;
//    }

}
