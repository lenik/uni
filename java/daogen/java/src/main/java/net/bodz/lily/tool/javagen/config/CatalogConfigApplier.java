package net.bodz.lily.tool.javagen.config;

import java.util.Map;

import net.bodz.bas.err.NoSuchKeyException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.*;
import net.bodz.bas.t.tuple.Split;

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
        for (String defCol : config.columnRefMap.keySet()) {
            IColumnMetadata column = table.getColumn(defCol);
            if (column == null) // not applicable.
                continue;
            CrossReference ref = table.getForeignKeyFromColumn(defCol);
            if (ref != null) // already defined
                continue;

            String refStr = config.columnRefMap.get(defCol);
            Split tabCol = Split.packageName(refStr);
            TableOid parentId = new TableOid();
            parentId.setFullName(tabCol.a);
            String parentColumnName = tabCol.b;

            ITableMetadata parent = table.getCatalog().getTable(parentId);
            if (parent == null)
                throw new NoSuchKeyException("can't resolve table: " + parentId);

            IColumnMetadata parentColumn = parent.getColumn(parentColumnName);
            if (parentColumn == null)
                throw new NoSuchKeyException("invalid column: " + parentColumnName + ", in " + parentId);

            String fake = "__fk_" + defCol;
            ref = new CrossReference();
            ref.setConstraintName(fake);

            TableKey key = new TableKey(table.getId(), column);
            TableKey parentKey = new TableKey(parentId, parentColumn);
            ref.setForeignKey(key);
            ref.setParentKey(parentKey);

            ref.setForeignTable(table);
            ref.setForeignColumns(new IColumnMetadata[] { column });

            ref.setParentTable(parent);
            ref.setParentColumns(new IColumnMetadata[] { parentColumn });

            ref.setJavaPackage(parent.getJavaPackage());
            ref.setJavaName(parent.getJavaName());

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
