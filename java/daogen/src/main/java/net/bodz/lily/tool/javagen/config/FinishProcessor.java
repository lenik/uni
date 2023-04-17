package net.bodz.lily.tool.javagen.config;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.Function;

import javax.persistence.Column;

import net.bodz.bas.c.string.Phrase;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.*;
import net.bodz.bas.text.Nullables;
import net.bodz.lily.model.base.CoEntity;
import net.bodz.lily.tool.javagen.ColumnName;
import net.bodz.lily.tool.javagen.TableName;

public class FinishProcessor
        implements
            ICatalogVisitor {

    static final Logger logger = LoggerFactory.getLogger(FinishProcessor.class);

    CatalogConfig config;

    public FinishProcessor(CatalogConfig config) {
        this.config = config;
    }

    @Override
    public void endTableOrView(ITableMetadata table) {
        setDefaultClassName(table);
        excludeInheritedColumns(table);
    }

    void setDefaultClassName(ITableMetadata table) {
        String simpleName = table.getJavaQName();
        String packageName = config.defaultPackageName;

        if (simpleName != null) {
            if (simpleName.contains(".")) {
                int lastDot = simpleName.lastIndexOf('.');
                packageName = simpleName.substring(0, lastDot);
                simpleName = simpleName.substring(lastDot + 1);
            }
        } else {
            ISchemaMetadata schema = table.getParent();

            String schemaJavaQName = schema.getJavaQName();
            if (schemaJavaQName != null)
                packageName += "." + schemaJavaQName;

            if (simpleName == null)
                simpleName = Phrase.foo_bar(table.getId().getTableName()).FooBar;
        }
        if (table instanceof DefaultTableMetadata) {
            DefaultTableMetadata mutable = (DefaultTableMetadata) table;
            mutable.setJavaPackage(packageName);
            mutable.setJavaName(simpleName);
        }
    }

    void excludeInheritedColumns(ITableMetadata tableView) {
        Class<?> superclass = CoEntity.class;

        String javaType = tableView.getJavaType();
        if (javaType != null) {
            try {
                superclass = Class.forName(javaType);
            } catch (ClassNotFoundException e) {
                logger.error(e, "unknown java type " + javaType);
                return;
            }
        }

        BeanInfo beanInfo;

        try {
            beanInfo = Introspector.getBeanInfo(superclass);
        } catch (IntrospectionException e) {
            logger.error(e, "error getting bean info for " + javaType);
            return;
        }

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method getter = pd.getReadMethod();
            if (getter == null)
                continue;

            String propertyName = pd.getName();
            String columnName;
            Column aColumn = getter.getAnnotation(Column.class);
            if (aColumn != null)
                columnName = aColumn.name();
            else
                columnName = Phrase.fooBar(propertyName).foo_bar;

            IColumnMetadata column = tableView.getColumn(columnName);
            if (column == null) {
                column = tableView.findColumnByJavaName(propertyName);
            }
            if (column != null) {
                if (column instanceof DefaultColumnMetadata) {
                    DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;
                    mutable.setExcluded(true);
                }
            }
        }
    }

    static final Function<String, String> trimUnderline = (String s) -> {
        while (s.endsWith("_"))
            s = s.substring(0, s.length() - 1);
        return s;
    };

    @Override
    public void foreignKey(ITableMetadata table, CrossReference crossRef) {
        IColumnMetadata[] columns = crossRef.getForeignKey().resolve(table);
        int n = columns.length;

        TableKey parentKey = crossRef.getParentKey();
        TableOid parentOid = parentKey.getId();

        ITableMetadata parentTable = table.getCatalog().getTable(parentOid);
        if (parentTable == null)
            throw new NullPointerException("parentTable");
        IColumnMetadata[] parentColumns = parentKey.resolve(parentTable);

        // check if error
        if (n != parentColumns.length)
            throw new IllegalUsageException(String.format(//
                    "Different column number: foreign key %d, parent key %d", //
                    n, parentColumns.length));

        crossRef.setForeignTable(table);
        crossRef.setParentTable(parentTable);
        crossRef.setForeignColumns(columns);
        crossRef.setParentColumns(parentColumns);

        ColumnName[] fv = config.columnNames(columns);
        ColumnName[] pv = config.columnNames(parentColumns);

        String property;

        // 1. fooSegment -> Foo.segment, fooSid -> foo.sid, ===> property = foo
        property = commonPrefix(fv, pv, //
                (ColumnName name) -> name.property, //
                (ColumnName name) -> Strings.ucfirst(name.property), //
                null);

        // 2. foo_seg -> foo.seg, foo_sid -> foo.sid, ===> property = foo
        if (property == null) {
            String columnPrefix = commonPrefix(fv, pv, //
                    (ColumnName name) -> name.column, //
                    trimUnderline);
            if (columnPrefix != null)
                property = StringId.UL.toCamel(columnPrefix);
        }

        // 3. cat -> foocat.sid, seg -> foocat.seg ===> property = cat
        if (property == null) {
            TableName parentName = config.tableName(parentTable);

            for (ColumnName f : fv)
                if (parentName.simpleClassName.endsWith(f.Property)) {
                    property = f.property;
                    break;
                }
            if (property == null) {
                if (n == 1)
                    // fallback to the property name for single column key.
                    property = fv[0].property;
                else
                    throw new IllegalUsageException("can't determine the xref property name.");
            }
        }

        crossRef.setJavaName(property);

        // Assemble descriptions.
        StringBuilder labels = null;
        StringBuilder descriptions = null;
        for (IColumnMetadata column : columns) {
            String label = column.getLabel();
            if (label != null) {
                if (labels == null)
                    labels = new StringBuilder();
                else
                    labels.append(" | ");
                labels.append(label);
            }
            String description = column.getDescription();
            if (description != null) {
                if (descriptions == null)
                    descriptions = new StringBuilder();
                else
                    descriptions.append("\n");
                descriptions.append(description);
            }
        }
        if (labels != null)
            crossRef.setLabel(labels.toString());
        if (descriptions != null)
            crossRef.setDescription(descriptions.toString());

        // Rename foreign column's java name to xrefProperty_id
        for (int i = 0; i < n; i++) {
            ColumnName f = fv[i];
            ColumnName p = pv[i];
            if (f.property.endsWith(p.Property)) {
                String prefix = f.property.substring(0, f.property.length() - p.property.length());
                if (!prefix.equals(property))
                    throw new UnexpectedException();
            } else {
                IColumnMetadata column = columns[i];
                if (column instanceof DefaultColumnMetadata) {
                    DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;
                    String normKeyProp = f.property + Strings.ucfirst(p.property);
                    String orig = column.getJavaName();
                    if (Nullables.notEquals(orig, normKeyProp))
                        mutable.setJavaName(normKeyProp);
                }
            }
        }
    }

    public static <T> String commonPrefix(T[] av, T[] bv, //
            Function<T, String> map, Function<String, String> norm) {
        return commonPrefix(av, bv, map, map, norm);
    }

    public static <T> String commonPrefix(T[] av, T[] bv, //
            Function<T, String> mapa, Function<T, String> mapb, //
            Function<String, String> norm) {
        int n = av.length;
        String common = null;
        for (int i = 0; i < n; i++) {
            String a = mapa.apply(av[i]);
            String b = mapb.apply(bv[i]);
            String prefix;
            if (a.endsWith(b))
                prefix = a.substring(0, a.length() - b.length());
            else {
                if (n == 1)
                    return null;
                else
                    prefix = a;
            }

            if (norm != null)
                prefix = norm.apply(prefix);

            if (common == null)
                common = prefix;
            else if (!common.equals(prefix)) {
                common = null;
                break;
            }
        }
        return common;
    }

}
