package net.bodz.lily.tool.javagen;

import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.IRowSetMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;

public class ColumnUtils {

    public static boolean isPreferredToGenerate(IColumnMetadata column) {
        if (column.isAutoIncrement(false))
            return true;
        if (column.isPrimaryKey()) {
            String defaultValue = column.getDefaultValue();
            if (defaultValue != null) {
                if (defaultValue.contains("nextval"))
                    return true;
            }
        }
        return false;
    }

    public static boolean isIgnoredInCreation(IColumnMetadata column) {
        if (isPreferredToGenerate(column))
            return true;
        return false;
    }

    public static final int GET_FIELD = 1;
    public static final int GET_GETTER = 2;
    public static final int GET_SETTER = 4;

    public static ColumnMember getMemberInfo(IColumnMetadata column, ColumnName n, int infoset) {
        IRowSetMetadata rowSet = column.getParent();
        if (!(rowSet instanceof ITableMetadata))
            return null;

        ITableMetadata table = (ITableMetadata) rowSet;
        ColumnMember m = new ColumnMember();
        m.declaringClassName = table.getJavaQName();
        try {
            m.declaringClass = Class.forName(m.declaringClassName);
            m.bestKnownClass = m.declaringClass;
        } catch (ClassNotFoundException e) {
            String superClassName = table.getJavaType();
            if (superClassName == null)
                return null;
            try {
                m.bestKnownClass = Class.forName(superClassName);
            } catch (ClassNotFoundException e1) {
                return m;
            }
        }

        Class<?> type = column.getType();
        boolean bool = type == boolean.class;

        if ((infoset & GET_FIELD) != 0)
            try {
                m.field = m.bestKnownClass.getField(n.field);
                type = m.field.getType();
            } catch (NoSuchFieldException e) {
            }

        if ((infoset & GET_GETTER) != 0)
            try {
                m.getter = m.bestKnownClass.getMethod((bool ? "is" : "get") + n.Property);
            } catch (NoSuchMethodException e) {
            }

        Class<?> argType = type;
        if (m.getter != null)
            argType = m.getter.getReturnType();

        if ((infoset & GET_SETTER) != 0)
            try {
                m.setter = m.bestKnownClass.getMethod("set" + n.Property, argType);
            } catch (NoSuchMethodException e) {
            }

        return m;
    }

}
