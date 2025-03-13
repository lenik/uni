package net.bodz.lily.tool.daogen.config;

import java.util.List;
import java.util.Map;

import net.bodz.bas.c.string.Phrase;
import net.bodz.bas.err.NoSuchKeyException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.cache.Derived;
import net.bodz.bas.t.catalog.ColumnOid;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.DefaultColumnMetadata;
import net.bodz.bas.t.catalog.DefaultTableMetadata;
import net.bodz.bas.t.catalog.ICatalogMetadata;
import net.bodz.bas.t.catalog.ICatalogVisitor;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ISchemaMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableKey;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.util.CanonicalClass;
import net.bodz.lily.tool.daogen.util.ReferencedTable;
import net.bodz.lily.tool.daogen.util.ReferencedTableMap;

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
                if (! javaName.contains("."))
                    javaName = config.defaultPackageName + "." + javaName;
                mutable.setJavaType(javaName);
            } else {
                setDefaultJavaType(mutable);
            }

            String base = config.getTableBase(table.getName());
            if (base != null)
                mutable.setBaseTypeName(base);
        }
        tableSettings = config.resolveTable(table.getName());
        return true;
    }

    void setDefaultJavaType(DefaultTableMetadata table) {
        QualifiedName qName = table.getJavaType();
        String simpleName;

        if (qName != null) {
            if (qName.packageName == null)
                qName = qName.packageName(config.defaultPackageName);
        } else {
            String packageName = config.defaultPackageName;

            ISchemaMetadata schema = table.getParent();

            QualifiedName schemaQName = schema.getJavaQName();
            if (schemaQName != null)
                packageName += "." + schemaQName;

            simpleName = Phrase.foo_bar(table.getId().getTableName()).FooBar;
            qName = new QualifiedName(packageName, simpleName);
        }
        table.setJavaType(qName);
    }

    @Override
    public void column(IColumnMetadata column) {
        ColumnSettings columnSettings = null;
        columnSettings = tableSettings.resolveColumn(column.getName());

        String propertyName = config.columnPropertyMap.get(column.getName());
        if (columnSettings.javaName == null)
            columnSettings.javaName = propertyName;

        if (column instanceof DefaultColumnMetadata) {
            DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;

            if (propertyName != null) {
                if (propertyName.isEmpty() || "-".equals(propertyName))
                    mutable.setExcluded(true);
                else {
                    mutable.setJavaQName(propertyName);
                    if (column.getAnnotation(Derived.class) != null)
                        mutable.setExcluded(true);
                }
            }

            Integer verboseLevel = config.columnLevelMap.get(column.getName());
            if (verboseLevel != null)
                mutable.setVerboseLevel(verboseLevel.intValue());

            Integer columnJoinLevel = config.columnJoinLevelMap.get(column.getName());
            if (columnJoinLevel != null)
                mutable.setJoinLevel(columnJoinLevel.intValue());

            if (columnSettings != null) {
                if (columnSettings.javaName != null)
                    mutable.setJavaQName(columnSettings.javaName);

                if (columnSettings.javaType != null) {
                    Class<?> javaClass;
                    try {
                        javaClass = CanonicalClass.forName(columnSettings.javaType);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    mutable.setJavaClass(javaClass);
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
        ICatalogMetadata catalog = table.getCatalog();
        if (catalog == null)
            throw new IllegalArgumentException("no catalog info.");

        ReferencedTableMap map = new ReferencedTableMap(table);
        DefaultTableMetadata mutable = (DefaultTableMetadata) table;

        for (String alias : config.columnRefMap.alias2QColumn.keySet()) {
            IColumnMetadata foreignColumn = table.getColumn(alias);
            if (foreignColumn == null)
                continue;
            if (foreignColumn.isForeignKey()) // already
                continue;

            ColumnOid parentColumnOid = config.columnRefMap.alias2QColumn.get(alias);

            ITableMetadata parent = catalog.getTable(parentColumnOid.getTable());
            if (parent == null)
                throw new NoSuchKeyException("invalid parent table: " + parentColumnOid.getTable());

            // IColumnMetadata parentColumn = parent.getColumn(parentColumnOid.getColumnName());
            ReferencedTable ref = map.resolveTable(parent);
            ref.add(parentColumnOid.getColumnName(), alias);
        }

        if (! map.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Auto added column-ref references:");
                map.dump();
            }
        }

        for (ReferencedTable ref : map.values()) {
            if (ref.isPrimaryKeyColumnsSet()) {
                List<CrossReference> fkeys = ref.buildForeignKeys(table);
                for (CrossReference fkey : fkeys) {
                    mutable.addForeignKey(fkey);
                    fkey.getForeignColumns();
                }
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
