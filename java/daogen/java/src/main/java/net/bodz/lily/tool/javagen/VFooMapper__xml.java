package net.bodz.lily.tool.javagen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableKey;

public class VFooMapper__xml
        extends JavaGen__xml {

    static final String SQLID_OBJLIST = "objlist_sql";
    static final String SQLID_OBJEDIT = "objedit_sql";

    public VFooMapper__xml(JavaGenProject project) {
        super(project, project.FooMapper);
    }

    @Override
    protected void buildXmlBody(XmlSourceBuffer out, ITableMetadata table) {
        out.println("<!DOCTYPE mapper");
        out.println("PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
        out.println("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");

        out.println("<mapper namespace=\"" + project.FooMapper + "\">");
        out.enter();
        {
            out.println();
            resultMap_objlist_map(out, table);
            out.println();
            // sql_objlist_sql(out, table, SQLID_OBJLIST);
            sql_objedit_sql(out, table, SQLID_OBJLIST);
            out.println();
            sql_objedit_sql(out, table, SQLID_OBJEDIT);
            out.println();
            sql_filtconds(out, table);
            out.println();
            select_all(out, table, SQLID_OBJLIST);
            out.println();
            select_filter(out, table, SQLID_OBJLIST);
            out.println();
            select(out, table, SQLID_OBJEDIT);
            out.println();
            select_count(out, table);
            out.println();
            out.leave();
        }
        out.println("</mapper>");
    }

    boolean isIncluded(IColumnMetadata column) {
        String javaName = column.getJavaName();
        if (javaName != null)
            if (javaName.isEmpty() || javaName.equals("-"))
                return false;
        return true;
    }

    boolean isUpdatable(IColumnMetadata column) {
        return !column.isReadOnly();
    }

    List<IColumnMetadata> getIncludedColumns(Iterable<IColumnMetadata> columns) {
        List<IColumnMetadata> list = new ArrayList<>();
        for (IColumnMetadata column : columns)
            if (isIncluded(column))
                list.add(column);
        return list;
    }

    void resultMap_objlist_map(XmlSourceBuffer out, ITableMetadata table) {
        JoinColumns j = new JoinColumns(project.config);
        j.addTable(table);

        out.printf("<resultMap id=\"objlist_map\" type=\"%s\">\n", project.Foo);
        out.enter();
        {
            List<IColumnMetadata> basicColumns = j.reorder(table.getColumns(), 3);
            for (IColumnMetadata column : basicColumns) {
                if (!isIncluded(column))
                    continue;
                map(out, table, column, j);
            }

            Set<String> aliases = j.aliasMap.keySet();
            for (String alias : aliases) {
                CrossReference ref = j.aliasMap.get(alias);
                ITableMetadata parent = ref.getParentTable();

                String mapperNs = RuntimeSupport.guessMapperNs(parent.getJavaQName());
                boolean defaultNs = mapperNs.equals(project.FooMapper.getFullName());
                String nsPrefix = defaultNs ? "" : (mapperNs + ".");

                out.printf("<association property=\"%s\" columnPrefix=\"%s\"\n", //
                        ref.getJavaName(), // property
                        alias + "_" // columnPrefix
                );
                out.printf("    javaType=\"%s\" \n", ref.getParentTable().getJavaQName());
                out.printf("    resultMap=\"%s\" />\n", nsPrefix + "objlist_map");
            }

            out.leave();
        }
        out.println("</resultMap>");
    }

    static final Set<String> ignoredProperties = new HashSet<>();
    static {
        ignoredProperties.add("-");
    }

    void map(XmlSourceBuffer out, ITableMetadata table, IColumnMetadata column, JoinColumns j) {
        ColumnName cname = project.columnName(column);
        // Class<?> type = column.getType();

        String property = cname.property;
        if (property == null || ignoredProperties.contains(property))
            return;

        String tag = column.isPrimaryKey() ? "id" : "result";
        out.printf("<%s property=\"%s\" column=\"%s\" />\n", //
                tag, templates.toProperty(column), cname.column);
    }

    void sql_objlist_sql(XmlSourceBuffer out, ITableMetadata table, String id) {
        List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());
        out.printf("<sql id=\"%s\"><![CDATA[\n", id);
        out.enter();
        {
            out.println("select");

            out.enter();
            templates.sqlColumnNameList(out, columns, "a.");
            out.println();
            out.leave();

            out.println("from " + table.getCompactName() + " a");

            out.println("]]>");
            out.leave();
        }
        out.println("</sql>");
    }

    void sql_objedit_sql(XmlSourceBuffer out, ITableMetadata table, String id) {
        List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());
        JoinColumns j = new JoinColumns(project.config);
        j.addTable(table);

        out.printf("<sql id=\"%s\"><![CDATA[\n", id);
        out.enter();
        {
            out.println("select");

            out.enter();
            templates.sqlColumnNameList(out, columns, "a.");
            for (String columnAlias : j.aliasColumns.keySet()) {
                AliasedColumn ac = j.aliasColumns.get(columnAlias);
                String columnName = ac.getColumn().getName();
                out.println(", ");
                out.printf("%s.%s %s_%s", //
                        DialectFn.quoteName(ac.tableAlias), //
                        DialectFn.quoteName(columnName), //
                        ac.tableAlias, //
                        columnName);
            }
            out.println();
            out.leave();

            out.println("from " + table.getCompactName() + " a");

            out.enter();
            for (String parentAlias : j.aliasMap.keySet()) {
                CrossReference ref = j.aliasMap.get(parentAlias);
                TableKey foreignKey = ref.getForeignKey();
                TableKey parentKey = ref.getParentKey();
                String refTable = parentKey.getId().getCompactName(table.getId());
                out.printf("left join %s %s", //
                        DialectFn.quoteQName(refTable), DialectFn.quoteName(parentAlias));
                String[] foreignColumns = foreignKey.getColumnNames();
                String[] parentColumns = parentKey.getColumnNames();
                for (int i = 0; i < foreignColumns.length; i++) {
                    out.print(i == 0 ? " on" : " and");
                    out.printf(" a.%s = %s.%s", //
                            DialectFn.quoteName(foreignColumns[i]), //
                            DialectFn.quoteName(parentAlias), //
                            DialectFn.quoteName(parentColumns[i]));
                }
                out.println();
            }

            out.leave();

            out.println("]]>");
            out.leave();
        }
        out.println("</sql>");
    }

    void sql_filtconds(XmlSourceBuffer out, ITableMetadata table) {
        List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());

        out.println("<sql id=\"filtconds\">");
        out.enter();
        {
            boolean includeCo = false;
            if (includeCo) {
                out.println("<!-- co -->");
                out.println("<include refid=\"co.modefilt\" />");
                out.println("<include refid=\"co.filter-id\" />");
                out.println("<include refid=\"co.filter-ui\" />");
                out.println("<include refid=\"co.filter-version\" />");
                out.println("<include refid=\"message.filter-all\" />");
            }

            for (IColumnMetadata column : columns) {
                filter(out, column);
            }
            out.leave();
        }
        out.println("</sql>");
    }

    void select_all(XmlSourceBuffer out, ITableMetadata table, String sqlId) {
        out.println("<select id=\"all\" resultMap=\"objlist_map\">");
        out.enter();
        {
            out.printf("<include refid=\"%s\" />\n", sqlId);
            out.println("<include refid=\"co.opts\" />");
            out.leave();
        }
        out.println("</select>");
    }

    void select_filter(XmlSourceBuffer out, ITableMetadata table, String sqlId) {
        out.println("<select id=\"filter\" resultMap=\"objlist_map\">");
        out.enter();
        {
            out.printf("<include refid=\"%s\" />\n", sqlId);
            out.println("<where>");
            out.enter();
            {
                out.println("<include refid=\"filtconds\" />");
                out.leave();
            }
            out.println("</where>");
            out.println("<include refid=\"co.opts\" />");
            out.leave();
        }
        out.println("</select>");
    }

    void select(XmlSourceBuffer out, ITableMetadata table, String sqlId) {
        out.println("<select id=\"select\" resultMap=\"objlist_map\">");
        out.enter();
        {
            out.printf("<include refid=\"%s\" />\n", sqlId);
            out.println("<where>");
            out.enter();
            {
                out.println("<if test=\"_parameter != null\">a.id = #{id}</if>");
                out.leave();
            }
            out.println("</where>");
            out.leave();
        }
        out.println("</select>");
    }

    void select_count(XmlSourceBuffer out, ITableMetadata table) {
        out.println("<select id=\"count\" resultType=\"long\">");
        out.enter();
        {
            out.println("select count(*) \"rows\" from " + table.getCompactName());
            out.println("<where>");
            out.enter();
            {
                out.println("<if test=\"_parameter != null\">");
                out.enter();
                {
                    out.println("<include refid=\"filtconds\" />");
                    out.leave();
                }
                out.println("</if>");
                out.leave();
            }
            out.println("</where>");
            out.leave();
        }
        out.println("</select>");
    }

    void filter(XmlSourceBuffer out, IColumnMetadata column) {
        ColumnName cname = project.columnName(column);

        // MaskFieldModel mask = column.mask;
        String maskProperty = cname.property;
        boolean hasMain = true;
        boolean hasRange = false;
        boolean hasPattern = false;

        int typeId = TypeKind.getTypeId(column.getType());
        switch (typeId) {
        case TypeId._byte:
        case TypeId._short:
        case TypeId._long:
        case TypeId._double:
        case TypeId._float:
        case TypeId._int:
        case TypeId.BYTE:
        case TypeId.SHORT:
        case TypeId.INTEGER:
        case TypeId.LONG:
        case TypeId.FLOAT:
        case TypeId.DOUBLE:

        case TypeId.BIG_INTEGER:
        case TypeId.BIG_DECIMAL:

        case TypeId.JODA_DATETIME:
            // TODO date-range or date-criteria?
            // more generic construction is needed.
            if (typeId == TypeId.JODA_DATETIME)
                hasMain = false;

        case TypeId.DATE:
        case TypeId.SQL_DATE:
            hasRange = true;

            if (hasMain) {
                out.printf("<if test=\"m.%s != null\">and %s = #{m.%s}</if>\n", //
                        maskProperty, cname.columnQuoted, maskProperty);
            }

            if (hasRange) {
                String range = maskProperty + "Range";
                out.printf("<if test=\"m.%s!= null\">\n", range);
                out.enter();
                out.printf("<if test=\"m.%s.hasStartIncl\">and a.%s >= #{m.%s.start}</if>\n", //
                        range, cname.columnQuoted, range);
                out.printf("<if test=\"m.%s.hasStartExcl\">and a.%s > #{m.%s.start}</if>\n", //
                        range, cname.columnQuoted, range);
                out.printf("<if test=\"m.%s.hasEndIncl\">and a.%s &lt;= #{m.%s.end}</if>\n", //
                        range, cname.columnQuoted, range);
                out.printf("<if test=\"m.%s.hasEndExcl\">and a.%s &lt; #{m.%s.end}</if>\n", //
                        range, cname.columnQuoted, range);
                out.leave();
                out.println("</if>");
            }
            break;

        case TypeId.STRING:
            hasPattern = true;

            if (hasMain)
                out.printf("<if test=\"m.%s != null\">and %s = #{m.%s}</if>\n", //
                        maskProperty, cname.columnQuoted, maskProperty);
            if (hasPattern)
                out.printf("<if test=\"m.%sPattern != null\">and %s like '${m.%sPattern}'</if>\n", //
                        maskProperty, cname.columnQuoted, maskProperty);
            break;

        // case TypeId.ENUM:

        case TypeId._char:
        case TypeId.CHARACTER:

        case TypeId._boolean:
        case TypeId.BOOLEAN:
        default:
            out.printf("<if test=\"m.%s != null\">and %s = #{m.%s}</if>\n", //
                    maskProperty, cname.columnQuoted, maskProperty);
        }
    }

}