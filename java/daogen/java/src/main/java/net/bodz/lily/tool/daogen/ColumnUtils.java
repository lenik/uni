package net.bodz.lily.tool.daogen;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.IRowSetMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.util.CanonicalClass;

public class ColumnUtils {

    static final Logger logger = LoggerFactory.getLogger(ColumnUtils.class);

    public static boolean isPreferredToGenerate(IColumnMetadata column) {
        if (column.isAutoIncrement())
            return true;
        if (column.isPrimaryKey()) {
            String defaultValue = column.getDefaultValue();
            if (defaultValue != null) {
                // if (defaultValue.contains("nextval"))
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

    public static ColumnMember getMemberInfo(IColumnMetadata column, ColumnNaming n, int infoset) {
        IRowSetMetadata rowSet = column.getParent();
        if (! (rowSet instanceof ITableMetadata))
            return null;

        ITableMetadata table = (ITableMetadata) rowSet;
        ColumnMember m = new ColumnMember();
        m.declaringClassName = table.getJavaTypeName();
        try {
            m.declaringClass = CanonicalClass.forName(m.declaringClassName);
            m.bestKnownClass = m.declaringClass;
        } catch (ClassNotFoundException e) {
            String superClassName = table.getBaseTypeName();
            if (superClassName == null)
                return null;
            try {
                m.bestKnownClass = CanonicalClass.forName(superClassName);
            } catch (ClassNotFoundException e1) {
                return m;
            }
        }

        Class<?> type = column.getJavaClass();
        boolean bool = type == boolean.class;

        if ((infoset & GET_FIELD) != 0)
            try {
                m.field = m.bestKnownClass.getField(n.fieldName);
                type = m.field.getType();
            } catch (NoSuchFieldException e) {
            }

        if ((infoset & GET_GETTER) != 0) {
            String methodName = (bool ? "is" : "get") + n.capPropertyName;
            try {
                m.getter = m.bestKnownClass.getMethod(methodName);
            } catch (NoSuchMethodException e) {
            } catch (Error e) {
                logger.errorf(e, "error get method %s.%s", m.bestKnownClass.getName(), methodName);
            }
        }

        Class<?> argType = type;
        if (m.getter != null)
            argType = m.getter.getReturnType();

        if ((infoset & GET_SETTER) != 0) {
            String methodName = "set" + n.capPropertyName;
            try {
                m.setter = m.bestKnownClass.getMethod(methodName, argType);
            } catch (NoSuchMethodException e) {
            } catch (Error e) {
                logger.errorf(e, "error get method %s.%s", m.bestKnownClass.getName(), methodName);
            }
        }

        return m;
    }

}
