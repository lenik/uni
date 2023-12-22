package net.bodz.lily.tool.daogen;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

import javax.persistence.Column;
import javax.persistence.Id;

import org.joda.time.DateTime;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.primitive.Primitives;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.meta.decl.Ordinal;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.provider.bean.BeanProperty;
import net.bodz.bas.potato.provider.reflect.FieldProperty;
import net.bodz.bas.repr.form.meta.TextInput;
import net.bodz.bas.repr.form.validate.NotNull;
import net.bodz.bas.repr.form.validate.Precision;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.DefaultColumnMetadata;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableKey;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.range.*;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.tool.daogen.util.JavaLang;

public class MiscTemplates {

    JavaGenProject project;

    public MiscTemplates(JavaGenProject project) {
        this.project = project;
    }

    public void javaDoc(ITreeOut out, String doc) {
        javaDoc(out, doc, 80);
    }

    public void javaDoc(ITreeOut out, String doc, int width) {
        if (doc == null)
            return;
        doc = doc.trim();
        if (doc.isEmpty())
            return;

        out.println("/**");
        out.println(" * " + doc);
        out.println(" */");
    }

    public QualifiedName getIdType(ITableMetadata table) {
        IColumnMetadata[] primaryKeyCols = table.getPrimaryKeyColumns();
        switch (primaryKeyCols.length) {
        case 0:
            return null;
        case 1:
            Class<?> kType = primaryKeyCols[0].getJavaClass();
            Class<?> kBoxed = Primitives.box(kType);
            return QualifiedName.parse(kBoxed.getCanonicalName());
        default:
            return project.IFoo_Id;
        }
    }

    @FunctionalInterface
    static interface CodeSnippet {
        String make(JavaSourceWriter out);
    }

    static Map<Class<?>, CodeSnippet> initVals = new HashMap<>();
    static {
        initVals.put(String.class, (JavaSourceWriter out) -> "\"\"");
        initVals.put(BigDecimal.class, (JavaSourceWriter out) -> "BigDecimal.ZERO");
        initVals.put(BigInteger.class, (JavaSourceWriter out) -> "BigInteger.ZERO");
        initVals.put(DateTime.class, (JavaSourceWriter out) -> {
            out.im.ref(DateTime.class);
            return "new DateTime(System.currentTimeMillis())";
        });
        initVals.put(Timestamp.class, (JavaSourceWriter out) -> {
            out.im.ref(Timestamp.class);
            return "new Timestamp(System.currentTimeMillis())";
        });
    }

    public void initNotNulls(JavaSourceWriter out, ITableMetadata table) {
        out.println("public void initNotNulls() {");
        out.enter();
        for (IColumnMetadata column : table.getColumns()) {
            if (column.isPrimaryKey())
                continue;

            if (column.isExcluded())
                continue;

            if (column.isNullable(true))
                continue;

            Class<?> type = column.getJavaClass();
            // System.out.printf("Column type: %s => %s\n", column.getName(), type);

            CodeSnippet initValSnippet = MiscTemplates.initVals.get(type);
            if (initValSnippet == null)
                continue;

            String initVal = initValSnippet.make(out);
            String code = getJavaSetCode(column, true, initVal);
            out.println("this." + code + ";");
        }
        out.leave();
        out.println("}");
    }

    public void FIELD_consts(ITreeOut out, ITableMetadata table, Boolean wantPrimaryKey) {
        List<String> defs = new ArrayList<>();
        for (IColumnMetadata column : table.getColumns()) {
            if (wantPrimaryKey != null)
                if (column.isPrimaryKey() != wantPrimaryKey.booleanValue())
                    continue;

            if (column.isExcluded())
                continue;

            if (column.isCompositeProperty())
                continue;

            ColumnName cname = project.columnName(column);
            defs.add("public static final String FIELD_" + cname.constField + " = "
                    + StringQuote.qqJavaString(cname.column) + ";");
        }
        if (!defs.isEmpty()) {
            out.println();
            for (String def : defs)
                out.println(def);
        }
    }

    public void N_consts(ITreeOut out, ITableMetadata table, Boolean wantPrimaryKey) {
        List<String> defs = new ArrayList<>();
        for (IColumnMetadata column : table.getColumns()) {
            if (wantPrimaryKey != null)
                if (column.isPrimaryKey() != wantPrimaryKey.booleanValue())
                    continue;

            if (column.isExcluded())
                continue;

            if (column.isCompositeProperty())
                continue;

            Class<?> type = column.getJavaClass();

            ColumnName cname = project.columnName(column);

            int precision = column.getPrecision();
            int scale = column.getScale();
            if (type == String.class || Number.class.isAssignableFrom(type)) {
            } else {
                if (precision < 0 && precision == Integer.MAX_VALUE)
                    precision = 0;
            }
            if (precision > 0) {
                String precisionVar = "N_" + cname.constField;
                defs.add("public static final int " + precisionVar + " = " + precision + ";");
                DefaultColumnMetadata m = (DefaultColumnMetadata) column;
                m.setPrecisionExpr(precisionVar);
            }
            if (scale > 0) {
                String scaleVar = "NS_" + cname.constField;
            }
        }
        if (!defs.isEmpty()) {
            out.println();
            for (String def : defs)
                out.println(def);
        }
    }

    public static final String OrdinalPrefix = "_ord_";

    public void ord_consts(ITreeOut out, ITableMetadata table, Boolean wantPrimaryKey) {
        List<String> defs = new ArrayList<>();
        String lastVarName = null;
        int lastOrdinal = 0;
        for (IColumnMetadata column : table.getColumns()) {
            if (wantPrimaryKey != null)
                if (column.isPrimaryKey() != wantPrimaryKey.booleanValue())
                    continue;

            if (column.isExcluded())
                continue;

            if (column.isCompositeProperty())
                continue;

            ColumnName cname = project.columnName(column);

            int ordinal = column.getOrdinal();
            String varName = OrdinalPrefix + cname.constField;

            String expr;
            int diff = ordinal - lastOrdinal;
            if (lastVarName != null && Math.abs(diff) <= 10)
                expr = lastVarName + (diff < 0 ? " - " + -diff : " + " + diff);
            else
                expr = "" + ordinal;
            defs.add("private static final int " + varName + " = " + expr + ";");

            lastVarName = varName;
            lastOrdinal = ordinal;
        }
        if (!defs.isEmpty()) {
            out.println();
            for (String def : defs)
                out.println(def);
        }
    }

    public void columnField(JavaSourceWriter out, IColumnMetadata column) {
        String javaType = project.config.javaType(column);

        String description = column.getDescription();
        if (description != null && !description.isEmpty())
            out.println("/** " + description + " */");

        if (column.isPrimaryKey())
            out.println("@" + out.im.name(Id.class));

        boolean notNull = !column.isNullable(true);
        if (notNull)
            out.println("@" + out.im.name(NotNull.class));

        ColumnName cname = project.columnName(column);
//        String fieldName = fieldOverride != null ? fieldOverride : cname.field;
        out.print(out.im.name(javaType) + " " + cname.field);
        out.println(";");
    }

    void columnGetterHeader(JavaSourceWriter out, IColumnMetadata column) {
        ColumnName n = project.columnName(column);
        String N_COL_NAME = "N_" + n.constField;

        Class<?> type = column.getJavaClass();

        String description = column.getDescription();
        if (description != null && !description.isEmpty()) {
            out.println("/**");
            out.println(" * " + description);
            out.println(" */");
        }

        if (column.isPrimaryKey()) {
            out.println("@" + out.im.name(Id.class));
        }

        int ordinal = column.getOrdinal();
        if (ordinal != 0) {
            String varName = OrdinalPrefix + n.constField;
            out.println("@" + out.im.name(Ordinal.class) + "(" + varName + ")");
        }

        boolean unique = column.isUnique();

        boolean notNull = !column.isNullable(true);
        if (notNull && !type.isPrimitive())
            out.println("@" + out.im.name(NotNull.class));

        // int columnDisplaySize = column.getColumnDisplaySize();
        int precision = column.getPrecision();
        int scale = column.getScale();

        if (type == String.class || Number.class.isAssignableFrom(type)) {

        }

        out.print("@" + out.im.name(Precision.class) + "(");
        {
            if (precision > 0 && //
                    (type == String.class || Number.class.isAssignableFrom(type))) {
                out.print("value = " + N_COL_NAME);
            } else {
                out.print("value = " + precision);
            }
            if (scale != 0)
                out.print(", scale = " + scale);
            out.println(")");
        }

        if (type == String.class) {
            if (precision > 0) {
                out.println("@" + out.im.name(TextInput.class) + "(maxLength = " + N_COL_NAME + ")");
            }
        }

        out.print("@" + out.im.name(Column.class));
        {
            out.print("(name = \"" + n.column + "\"");
            if (unique)
                out.print(", unique = true");
            if (notNull)
                out.print(", nullable = false");

            boolean insertable = true;
            boolean updatable = true;
            if (!insertable)
                out.print(", insertable = false");
            if (!updatable)
                out.print(", updatable = false");

            if (type == String.class)
                out.print(", length = " + N_COL_NAME);
            else {
                out.print(", precision = " + precision);
                if (scale != 0)
                    out.print(", scale = " + scale);
            }
            out.println(")");
        }
    }

    void columnSetterHeader(JavaSourceWriter out, IColumnMetadata column) {
        String description = column.getDescription();
        if (description != null && !description.isEmpty()) {
            out.println("/**");
            out.println(" * " + description);
            out.println(" */");
        }
    }

    /**
     * @Id
     * @Ordinal
     * @NotNull
     * @Precision
     * @TextInput
     * @Column
     */
    public void columnAccessors(JavaSourceWriter out, IColumnMetadata column, boolean impl) {
        ColumnName n = project.columnName(column);
        String javaType = project.config.javaType(column);
        boolean notNull = !column.isNullable(true);
        String isOrGet = "boolean".equals(javaType) ? "is" : "get";

        columnGetterHeader(out, column);
        out.printf("public %s %s%s()", //
                out.im.name(javaType), isOrGet, n.Property);
        if (impl) {
            out.println(" {");
            out.printf("    return %s;\n", n.field);
            out.println("}");
        } else {
            out.println(";");
        }
        out.println();

        columnSetterHeader(out, column);
        out.printf("public void set%s(%s%s value)", n.Property, //
                (notNull && !JavaLang.isPrimitive(javaType)) //
                        ? ("@" + out.im.name(NotNull.class) + " ")
                        : "",
                out.im.name(javaType));
        if (impl) {
            out.println(" {");
            out.printf("    this.%s = value;\n", n.field);
            out.println("}");
        } else {
            out.println(";");
        }
    }

    public void foreignKeyField(JavaSourceWriter out, CrossReference xref, ITableMetadata table) {
        TableKey foreignKey = xref.getForeignKey();
        IColumnMetadata[] columns = foreignKey.resolve(table);
        TableOid parentOid = xref.getParentKey().getId();
        ITableMetadata parentTable = project.catalog.getTable(parentOid);
        if (parentTable == null) {
            throw new UnexpectedException("parent is not defined: " + parentOid);
        }

        String description = xref.getDescription();
        String inheritDocFrom = null;
        if (description == null) {
            description = parentTable.getDescription();
            if (description != null)
                inheritDocFrom = out.im.name(parentTable.getJavaQName());
        }

        out.print("/** ");
        if (Nullables.isNotEmpty(description)) {
            if (inheritDocFrom != null)
                out.print("(" + description + ")");
            else
                out.print(description);
        }
        out.println(" */");

        boolean notNull = false;
        for (IColumnMetadata c : columns)
            if (!c.isNullable(false)) {
                notNull = true;
                break;
            }
        if (notNull)
            out.println("@" + out.im.name(NotNull.class));

        String parentType = parentTable.getJavaQName();
        if (parentType == null)
            throw new NullPointerException("parentType");

        String property = xref.getJavaName();

        out.print(out.im.name(parentType) + " " + property);
        out.println(";");
    }

    public void foreignKeyAccessors(JavaSourceWriter out, CrossReference xref, ITableMetadata table) {
        TableKey foreignKey = xref.getForeignKey();
        IColumnMetadata[] columns = foreignKey.resolve(table);
        TableOid parentOid = xref.getParentKey().getId();
        ITableMetadata parentTable = project.catalog.getTable(parentOid);
        if (parentTable == null) {
            throw new UnexpectedException("parent is not defined: " + parentOid);
        }

        String label = xref.getLabel();
        String description = xref.getDescription();
        String inheritDocFrom = null;
        if (description == null) {
            description = parentTable.getDescription();
            if (description != null)
                inheritDocFrom = out.im.name(parentTable.getJavaQName());
        }

        out.println("/**");
        if (Nullables.isNotEmpty(description)) {
            if (inheritDocFrom != null)
                out.println(" * {inheritDoc " + inheritDocFrom + "}");
            out.println(" * " + description);
        }
        out.println(" *");
        if (Nullables.isNotEmpty(label))
            out.println(" * @label " + label);
        out.println(" * @constraint " + xref.getForeignKeySQL());
        out.println(" */");

        boolean notNull = false;
        for (IColumnMetadata c : columns)
            if (!c.isNullable(false)) {
                notNull = true;
                break;
            }
        if (notNull)
            out.println("@" + out.im.name(NotNull.class));

        String parentType = parentTable.getJavaQName();
        if (parentType == null)
            throw new NullPointerException("parentType");

        String property = xref.getJavaName();
        String Property = Strings.ucfirst(property);

        out.printf("public %s get%s()", //
                out.im.name(parentType), Property);
        out.println(" {");
        out.printf("    return %s;\n", property);
        out.println("}");
        out.println();

        out.println("/**");
        if (description != null && !description.isEmpty())
            out.println(" * " + description);
        out.println(" */");

        out.printf("public void set%s(%s%s value)", Property, //
                notNull ? ("@" + out.im.name(NotNull.class) + " ") : "", out.im.name(parentType));
        out.println(" {");
        out.printf("    this.%s = value;\n", property);
        out.println("}");
    }

    /**
     * @Id
     * @Ordinal
     * @NotNull
     * @Precision
     * @TextInput
     * @Column
     */
    public void foreignKeyColumnAccessors(JavaSourceWriter out, CrossReference xref, IColumnMetadata column,
            IColumnMetadata parentColumn, boolean impl) {
        ColumnName n = project.columnName(column);
        ColumnName p = project.columnName(parentColumn);

        Class<?> returnType = column.getJavaClass();
        boolean notNull = !column.isNullable(true);
        String isOrGet = boolean.class == returnType ? "is" : "get";

        String refFieldName = xref.getJavaName();

        boolean parentNullable = parentColumn.isNullable(false);
        if (parentNullable == false) {
            ColumnMember m = ColumnUtils.getMemberInfo(parentColumn, p, //
                    ColumnUtils.GET_GETTER);
            if (m != null && m.getter != null)
                parentNullable = !m.getter.getReturnType().isPrimitive();
        }

        columnGetterHeader(out, column);
        out.printf("public synchronized %s %s%s()", //
                out.im.name(returnType), isOrGet, n.Property);
        if (impl) {
            out.enterln(" {");
            out.printf("if (%s != null)", refFieldName);
            out.enterln(" {");
            {
                if (returnType.isPrimitive()) { // non-null
                    if (parentNullable) {
                        out.printf("if (%s.%s%s() == null)\n", refFieldName, isOrGet, p.Property);
                        out.printf("    return %s;\n", nullDefault(returnType));
                    }
                }
                out.printf("return %s.%s%s();\n", refFieldName, isOrGet, p.Property);
                out.leaveln("}");
            }
            out.printf("return %s;\n", n.field);
            out.leaveln("}");
        } else {
            out.println(";");
        }
        out.println();

        columnSetterHeader(out, column);
        out.printf("public synchronized void set%s(%s%s value)", n.Property, //
                (notNull && !returnType.isPrimitive()) ? ("@" + out.im.name(NotNull.class) + " ") : "",
                out.im.name(returnType));
        if (impl) {
            out.enterln(" {");
            if (!project.parentColumnInParallelMode)
                out.printf("this.%s = null;\n", refFieldName);
            out.printf("this.%s = value;\n", n.field);
            out.leaveln("}");
        } else {
            out.println(";");
        }
    }

    static Map<Class<?>, String> nullDefaults;
    static {
        nullDefaults = new HashMap<>();
        nullDefaults.put(byte.class, "(byte) 0");
        nullDefaults.put(short.class, "(short) 0");
        nullDefaults.put(int.class, "0");
        nullDefaults.put(long.class, "0L");
        nullDefaults.put(float.class, "0.0f");
        nullDefaults.put(double.class, "0.0");
        nullDefaults.put(boolean.class, "false");
        nullDefaults.put(char.class, "0");
    }

    static String nullDefault(Class<?> type) {
        String code = nullDefaults.get(type);
        if (code == null)
            code = "(no default)";
        return code;
    }

    static Map<Class<?>, Class<?>> rangeMapping;
    static {
        rangeMapping = new HashMap<>();
        rangeMapping.put(Short.class, ShortRange.class);
        rangeMapping.put(Integer.class, IntegerRange.class);
        rangeMapping.put(Long.class, LongRange.class);
        rangeMapping.put(Float.class, FloatRange.class);
        rangeMapping.put(Double.class, DoubleRange.class);

        rangeMapping.put(BigInteger.class, BigIntegerRange.class);
        rangeMapping.put(BigDecimal.class, BigDecimalRange.class);

        rangeMapping.put(Date.class, DateTimeRange.class);
        rangeMapping.put(java.sql.Date.class, DateTimeRange.class);
        rangeMapping.put(Timestamp.class, DateTimeRange.class);
    }

    public void columnMaskFields(JavaSourceWriter out, IColumnMetadata column) {
        ColumnName cname = project.columnName(column);
        Class<?> type = Primitives.box(column.getJavaClass());

        String description = column.getDescription();
        if (description != null && !description.isEmpty())
            out.println("/** " + description + " */");

        out.println(out.im.name(type) + " " + cname.field + ";");

        if (type == String.class)
            out.println("String " + cname.field + "Pattern;");
        else {
            Class<?> rangeType = rangeMapping.get(type);
            if (rangeType != null)
                out.printf("%s %sRange = new %s();\n", //
                        out.im.name(rangeType), //
                        cname.field, //
                        out.im.name(rangeType));
        }
    }

    public void columnMaskAccessors(JavaSourceWriter out, IColumnMetadata column) {
        ColumnName cname = project.columnName(column);
        Class<?> type = Primitives.box(column.getJavaClass());

        String description = column.getDescription();
        if (description != null && !description.isEmpty()) {
            out.println("/** " + description + " */");
        }

        out.printf("public %s get%s() {\n", //
                out.im.name(type), cname.Property);
        out.printf("    return %s;\n", cname.field);
        out.println("}");
        out.println();

        if (description != null && !description.isEmpty()) {
            out.println("/** " + description + " */");
        }
        out.printf("public void set%s(%s value) {\n", cname.Property, //
                out.im.name(type));
        out.printf("    this.%s = value;\n", cname.field);
        out.println("}");

        if (type == String.class) {
            out.println();
            out.printf("public String get%sPattern() {\n", cname.Property);
            out.printf("    return %sPattern;\n", cname.field);
            out.println("}");
            out.println();
            out.printf("public void set%sPattern(%s value) {\n", cname.Property, //
                    out.im.name(type));
            out.printf("    this.%sPattern = value;\n", cname.field);
            out.println("}");
        } else {
            Class<?> rangeType = rangeMapping.get(type);
            if (rangeType != null) {
                String RT = out.im.name(rangeType);
                out.println();
                out.printf("public %s get%sRange() {\n", RT, cname.Property);
                out.printf("    return %sRange;\n", cname.field);
                out.println("}");
                out.println();
                out.printf("public void set%sRange(%s range) {\n", cname.Property, RT);
                out.printf("    this.%sRange = range;\n", cname.field);
                out.println("}");
            }
        }
    }

    public void sqlColumnNameList(ITreeOut out, List<IColumnMetadata> columns, String prefix) {
        int n = columns.size();
        for (int i = 0; i < n; i++) {
            IColumnMetadata column = columns.get(i);
            ColumnName cname = project.columnName(column);
            out.print(prefix + cname.columnQuoted);
            if (i != n - 1) {
                out.println(", ");
            }
        }
    }

    public void sqlMatchPrimaryKey(ITreeOut out, IColumnMetadata[] keyColumns) {
        if (keyColumns.length == 0)
            throw new IllegalUsageException("Can't update table without primary key.");

        if (keyColumns.length == 1) {
            IColumnMetadata column = keyColumns[0];
            ColumnName cname = project.columnName(column);
            out.println(cname.columnQuoted + " = #{id}");
        } else {
            for (int i = 0; i < keyColumns.length; i++) {
                IColumnMetadata column = keyColumns[i];
                ColumnName cname = project.columnName(column);
                if (i != 0)
                    out.print("and ");
                out.println(cname.columnQuoted + " = " + toSqlVar(column));
            }
        }
    }

    public String toProperty(IColumnMetadata column) {
        ColumnName cname = project.columnName(column);
//        Class<?> type = column.getType();  type == Object.class
//        if (JsonMap.class.isAssignableFrom(type)) {
        switch (column.getSqlTypeName()) {
        case "json":
        case "jsonb":
            return cname.property + ".jsonStr";
        default:
            return cname.property;
        }
    }

    public String toSqlVar(IColumnMetadata column) {
        ColumnName cname = project.columnName(column);
//        Class<?> type = column.getType();
//        if (JsonMap.class.isAssignableFrom(type)) {
        switch (column.getSqlTypeName()) {
        case "json":
        case "jsonb":
            return String.format("#{%s.jsonStr}::jsonb", cname.property);
        default:
            return String.format("#{%s}", cname.property);
        }
    }

    public static String getContextPrefix(String path) {
        StringBuilder sb = new StringBuilder();
        int dot;
        while ((dot = path.indexOf('.')) != -1) {
            String field = path.substring(0, dot);
            path = path.substring(dot + 1);
            sb.append("get" + Strings.ucfirst(field) + "().");
        }
        String prefix = sb.toString();
        return prefix;
    }

    /**
     * @return <code>null</code> if property is un-readable.
     */
    public String getJavaGetExpr(IColumnMetadata column) {
        ColumnName cname = project.columnName(column);
        IProperty property = column.getProperty();
        if (property != null) {
            String prefix = getContextPrefix(cname.property);
            if (property instanceof FieldProperty) {
                FieldProperty fp = (FieldProperty) property;
                return prefix + fp.getName();
            } else if (property instanceof BeanProperty) {
                Method getter = ((BeanProperty) property).getReadMethod();
                if (getter == null)
                    return null;
                return prefix + getter.getName() + "()";
            } else
                throw new UnsupportedOperationException();
        }
        return cname.field;
    }

    /**
     * @return <code>null</code> if property is un-writable.
     */
    public String getJavaSetCode(IColumnMetadata column, boolean directAccess, String javaExpr) {
        ColumnName cname = project.columnName(column);
        IProperty property = column.getProperty();
        if (property != null //
                && (!directAccess || column.isCompositeProperty())) {
            String context = getContextPrefix(cname.property);
            if (property instanceof FieldProperty) {
                FieldProperty fp = (FieldProperty) property;
                return context + fp.getName() + " = " + javaExpr;
            } else if (property instanceof BeanProperty) {
                Method setter = ((BeanProperty) property).getWriteMethod();
                if (setter == null)
                    return null;
                return context + setter.getName() + "(" + javaExpr + ")";
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return cname.field + " = " + javaExpr;
    }

    Set<String> getCompositeHeads(ITableMetadata table) {
        Set<String> compositeHeads = new HashSet<>();
        for (String fkName : table.getForeignKeys().keySet()) {
            CrossReference xref = table.getForeignKeys().get(fkName);
            if (xref.isCompositeProperty()) {
                IColumnMetadata column = xref.getForeignColumns()[0];
                String propertyPath = column.getJavaName();
                String head = Split.headDomain(propertyPath).a;
                compositeHeads.add(head);
            }
        }

        for (IColumnMetadata column : table.getColumns()) {
            if (column.isExcluded()) // mixin?
                continue;
            if (column.isCompositeProperty()) {
                String propertyPath = column.getJavaName();
                String head = Split.headDomain(propertyPath).a;
                compositeHeads.add(head);
            }
        }
        return compositeHeads;
    }

}