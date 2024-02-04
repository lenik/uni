package net.bodz.lily.tool.daogen.config;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.function.Function;

import javax.persistence.Column;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.Phrase;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.*;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.concrete.IdEntity;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.TableName;
import net.bodz.lily.tool.daogen.util.CanonicalClass;

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
        if (table instanceof DefaultTableMetadata) {
            DefaultTableMetadata mutable = (DefaultTableMetadata) table;
            setDefaultClassName(mutable);
            excludeInheritedColumns(mutable);
            bindProperties(mutable);
        }
    }

    void setDefaultClassName(DefaultTableMetadata table) {
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
        table.setJavaPackage(packageName);
        table.setJavaName(simpleName);
    }

    void excludeInheritedColumns(DefaultTableMetadata tableView) {
        Class<?> superclass = IdEntity.class;

        String parentType = tableView.getBaseTypeName();
        if (parentType != null) {
            try {
                superclass = CanonicalClass.forName(parentType);
            } catch (ClassNotFoundException e) {
                logger.warn("unknown java type " + parentType);
                return;
            }
        }

        BeanInfo superInfo;

        try {
            superInfo = Introspector.getBeanInfo(superclass);
        } catch (Throwable e) {
            logger.error(e, "error getting bean info for " + parentType);
            return;
        }

        for (PropertyDescriptor superProperty : superInfo.getPropertyDescriptors()) {
            Method superGetter = superProperty.getReadMethod();
            if (superGetter == null)
                continue;

            String superPropertyName = superProperty.getName();
            String superColumnName;
            Column aSuperColumn = superGetter.getAnnotation(Column.class);
            if (aSuperColumn != null)
                superColumnName = aSuperColumn.name();
            else
                superColumnName = Phrase.fooBar(superPropertyName).foo_bar;

            IColumnMetadata superColumn = tableView.getColumn(superColumnName);
            if (superColumn == null) {
                superColumn = tableView.findColumnByJavaName(superPropertyName);
            }
            if (superColumn != null) {
                if (superColumn instanceof DefaultColumnMetadata) {
                    DefaultColumnMetadata _superColumn = (DefaultColumnMetadata) superColumn;
                    _superColumn.setExcluded(true);
                }
            }
        }
    }

    /**
     * Bind runtime IProperty to the table metadata.
     */
    void bindProperties(DefaultTableMetadata table) {
        IType type = table.getEntityType();
        if (type == null)
            return;

        for (IColumnMetadata column : table.getColumns()) {
            DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;
            ColumnNaming cname = config.naming(column);
            IProperty property;
            if (column.isCompositeProperty()) {
                property = getPathProperty(type, cname.propertyName);
            } else {
                property = type.getProperty(cname.propertyName);
            }
            if (property != null)
                mutable.setProperty(property);
        }
    }

    IProperty getPathProperty(IType type, String path) {
        if (type == null)
            throw new NullPointerException("type");
        if (path == null)
            throw new NullPointerException("path");
        Split split = Split.headDomain(path);
        IProperty property = type.getProperty(split.a);
        if (property == null)
            return null;
        if (split.b == null)
            return property;
        IProperty leaf = getPathProperty(property.getPropertyType(), split.b);
        return leaf;
    }

    static final Function<String, String> trimRightUnderline = (String s) -> {
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

        ColumnNaming[] fv = config.naming(columns);
        ColumnNaming[] pv = config.naming(parentColumns);

        String property;

        // 1. [ property = foo ]
        // fk property fooSegment -> parent property *.segment,
        // fk property fooSid -> parent property *.sid
        property = trimSuffix(fv, pv, //
                (ColumnNaming name) -> name.propertyName, //
                (ColumnNaming name) -> Strings.ucfirst(name.propertyName), //
                null);

        // 2. [ property = foo ]:
        // fk column foo_seg -> parent column foo.seg
        // fk column foo_sid -> parent column foo.sid
        if (property == null) {
            String columnHead = trimSuffix(fv, pv, //
                    (ColumnNaming name) -> name.column, //
                    trimRightUnderline);
            if (!Nullables.isEmpty(columnHead))
                property = StringId.UL.toCamel(columnHead);
        }

        if (n == 1) {
            ColumnNaming column = fv[0];
            KeyColumnNameInfo info = config.keyColumnSettings.parseColumnByAnyFormat(column.column);
            if (info != null) {
                property = info.getAliasProperty();
                fv[0].initByPropertyName(info.getAliasProperty());
                pv[0].initByPropertyName(info.getFieldProperty());
            }
        }

        // 4. [ property = cat ]:
        // fk column cat -> parent column foocat.key_a
        // fk column key_b -> parent column foocat.key_b
        if (property == null) {
            TableName parentName = config.defaultTableName(parentTable);
            for (ColumnNaming f : fv)
                if (parentName.simpleClassName.endsWith(f.ucfirstPropertyName)) {
                    property = f.propertyName;
                    break;
                }
        }

        if (property == null) {
            if (n == 1)
                // fallback to the property name for single column key.
                property = fv[0].propertyName;
            else
                throw new IllegalUsageException("can't determine the xref property name.");
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
            ColumnNaming f = fv[i];
            ColumnNaming p = pv[i];
            if (f.propertyName.endsWith(p.ucfirstPropertyName)) {
                String head = f.propertyName.substring(0, f.propertyName.length() - p.propertyName.length());
                if (!head.equals(property))
                    throw new UnexpectedException();
            } else {
                IColumnMetadata column = columns[i];
                if (column instanceof DefaultColumnMetadata) {
                    DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;
                    String fkFieldProp = property + Strings.ucfirst(p.propertyName);
                    String orig = column.getJavaName();
                    if (Nullables.notEquals(orig, fkFieldProp))
                        mutable.setJavaName(fkFieldProp);
                }
            }
        }
    }

    public static <T> String trimSuffix(T[] av, T[] bv, //
            Function<T, String> map, Function<String, String> norm) {
        return trimSuffix(av, bv, map, map, norm);
    }

    /**
     * @return <code>null</code> if no common prefix
     */
    public static <T> String trimSuffix(T[] array, T[] suffixArray, //
            Function<T, String> mapa, Function<T, String> mapb, //
            Function<String, String> norm) {
        int n = array.length;
        if (n == 0)
            return null;

        String commonHead = null;
        for (int i = 0; i < n; i++) {
            String a = mapa.apply(array[i]);
            String suffix = mapb.apply(suffixArray[i]);
            String head;
            if (a.endsWith(suffix)) {
                head = a.substring(0, a.length() - suffix.length());
            } else {
                if (n == 1)
                    return null;
                else
                    head = a;
            }

            if (norm != null)
                head = norm.apply(head);

            if (commonHead == null)
                commonHead = head;
            else if (!commonHead.equals(head))
                return null;
        }
        return commonHead;
    }

}
