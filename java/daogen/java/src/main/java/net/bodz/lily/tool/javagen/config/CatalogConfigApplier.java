package net.bodz.lily.tool.javagen.config;

import java.util.Map;

import net.bodz.bas.err.NoSuchKeyException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.*;
import net.bodz.lily.tool.javagen.util.ReferencedTable;
import net.bodz.lily.tool.javagen.util.ReferencedTableMap;

public class CatalogConfigApplier
        implements
            ICatalogVisitor {

    static final Logger logger = LoggerFactory.getLogger(CatalogConfigApplier.class);

    CatalogConfig config;
    TableSettings tableSettings;

    public CatalogConfigApplier(CatalogConfig config) {
        this.config = config;
    }

    @Override
    public boolean beginTableOrView(ITableMetadata table) {
        if (table instanceof DefaultTableMetadata) {
            DefaultTableMetadata mutable = (DefaultTableMetadata) table;

            String javaName = config.tableNameMap.get(table.getName());
            if (javaName != null) {
                if (!javaName.contains("."))
                    javaName = config.defaultPackageName + "." + javaName;
                mutable.setJavaQName(javaName);
            }

            String type = config.getTableClassMap().get(table.getName());
            if (type != null)
                mutable.setJavaType(type);

        }
        tableSettings = config.resolveTable(table.getName());
        return true;
    }

    @Override
    public void column(IColumnMetadata column) {
        ColumnSettings columnSettings = null;
        columnSettings = tableSettings.resolveColumn(column.getName());

        String property = config.columnPropertyMap.get(column.getName());
        if (columnSettings.javaName == null)
            columnSettings.javaName = property;

        if (column instanceof DefaultColumnMetadata) {
            DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;

            if (property != null) {
                if (property.isEmpty() || "-".equals(property))
                    mutable.setExcluded(true);
                else
                    mutable.setJavaName(property);
            }

            Integer verboseLevel = config.columnLevelMap.get(column.getName());
            if (verboseLevel != null)
                mutable.setVerboseLevel(verboseLevel.intValue());

            Integer joinLevel = config.joinLevelMap.get(column.getName());
            if (joinLevel != null)
                mutable.setJoinLevel(joinLevel.intValue());

            if (columnSettings != null) {
                if (columnSettings.javaName != null)
                    mutable.setJavaName(columnSettings.javaName);

                if (columnSettings.javaType != null) {
                    Class<?> javaClass;
                    try {
                        javaClass = Class.forName(columnSettings.javaType);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    mutable.setType(javaClass);
                }

                if (columnSettings.description != null)
                    mutable.setDescription(columnSettings.description);
            }
        }
    }

    @Override
    public void endTableOrView(ITableMetadata table) {
        applyMapTuples(table);
    }

    void applyMapTuples(ITableMetadata table) {
        ReferencedTableMap map = new ReferencedTableMap(table);

        for (String alias : config.columnRefMap.alias2QColumn.keySet()) {
            IColumnMetadata foreignColumn = table.getColumn(alias);
            if (foreignColumn == null)
                continue;
            if (foreignColumn.isForeignKey()) // already
                continue;

            ColumnOid parentColumnOid = config.columnRefMap.alias2QColumn.get(alias);

            ICatalogMetadata catalog = table.getCatalog();
            if (catalog == null)
                throw new IllegalArgumentException("no catalog info.");

            ITableMetadata parent = catalog.getTable(parentColumnOid.getTable());
            if (parent == null)
                throw new NoSuchKeyException("invalid parent table: " + parentColumnOid.getTable());

            // IColumnMetadata parentColumn = parent.getColumn(parentColumnOid.getColumnName());
            ReferencedTable ref = map.resolveTable(parent);
            ref.add(parentColumnOid.getColumnName(), alias);
        }

        if (!map.isEmpty())
            map.dump();

        for (ReferencedTable ref : map.values()) {
            if (ref.isPrimaryKeyColumnsSet()) {
                CrossReference fkey = ref.buildForeignKey(table);
                DefaultTableMetadata mutable = (DefaultTableMetadata) table;
                mutable.addForeignKey(fkey);
            }
        }
    }

    void applySingleMaps(ITableMetadata table) {
        for (String columnAlias : config.columnRefMap.alias2QColumn.keySet()) {
            IColumnMetadata column = table.getColumn(columnAlias);
            if (column == null) // not applicable.
                continue;
            CrossReference ref = table.getForeignKeyFromColumn(columnAlias);
            if (ref != null) // already defined
                continue;
            ref = new CrossReference();

            ColumnOid qCol = config.columnRefMap.alias2QColumn.get(columnAlias);

            ITableMetadata parent = table.getCatalog().getTable(qCol.getTable());
            if (parent == null)
                throw new NoSuchKeyException("can't resolve table: " + qCol.getTable());

            IColumnMetadata parentColumn = parent.getColumn(qCol.getColumnName());
            if (parentColumn == null)
                throw new NoSuchKeyException("invalid column: " + qCol.getColumnName() + ", in " + qCol.getTable());

            String fake = "__fk_" + columnAlias;
            ref.setConstraintName(fake);
            ref.manyToOne(table, column, parent, parentColumn);

            Map<String, CrossReference> foreignKeys = table.getForeignKeys();
            foreignKeys.put(fake, ref);
        }
    }

    @Override
    public void primaryKey(ITableMetadata table, TableKey key) {
    }

    @Override
    public void foreignKey(ITableMetadata table, CrossReference crossRef) {
    }

}
