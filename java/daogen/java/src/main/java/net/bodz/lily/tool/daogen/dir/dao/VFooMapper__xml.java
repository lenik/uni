package net.bodz.lily.tool.daogen.dir.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.ITableUsage;
import net.bodz.bas.t.catalog.IViewMetadata;
import net.bodz.bas.t.catalog.TableKey;
import net.bodz.lily.tool.daogen.AliasedColumn;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__xml;
import net.bodz.lily.tool.daogen.JoinColumns;
import net.bodz.lily.tool.daogen.RuntimeSupport;
import net.bodz.lily.tool.daogen.util.DialectFn;

public class VFooMapper__xml
        extends JavaGen__xml {

    static final Logger logger = LoggerFactory.getLogger(VFooMapper__xml.class);

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

        out.println("<mapper namespace=\"" + project.FooMapper.qName + "\">");
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
        return ! column.isReadOnly();
    }

    List<IColumnMetadata> getIncludedColumns(Iterable<? extends IColumnMetadata> columns) {
        List<IColumnMetadata> list = new ArrayList<>();
        for (IColumnMetadata column : columns)
            if (isIncluded(column))
                list.add(column);
        return list;
    }

    void resultMap_objlist_map(XmlSourceBuffer out, ITableMetadata table) {
        JoinColumns j = new JoinColumns(project.config);
        j.addTable(table);

        out.printf("<resultMap id=\"objlist_map\" type=\"%s\">\n", //
                project.Foo.qName);
        out.enter();
        {
            List<IColumnMetadata> basicColumns = j.reorder(table.getColumns(), 3);
            for (IColumnMetadata column : basicColumns) {
                if (! isIncluded(column))
                    continue;
                map(out, table, column, j);
            }

            Set<String> aliases = j.aliasMap.keySet();
            for (String alias : aliases) {
                CrossReference ref = j.aliasMap.get(alias);
                ITableMetadata parent = ref.getParentTable();

                String mapperNs = RuntimeSupport.guessMapperNs(parent.getJavaType().getFullName());
                boolean defaultNs = mapperNs.equals(project.FooMapper.qName.getFullName());
                String nsPrefix = defaultNs ? "" : (mapperNs + ".");

                String[] parentKeyCols = parent.getPrimaryKey().getColumnNames();
                String notNullColumns = StringArray.join(", ", parentKeyCols);

                out.printf("<association property=\"%s\" columnPrefix=\"%s\" notNullColumn=\"%s\" \n", //
                        ref.getPropertyName(), // property
                        alias + "_", // columnPrefix
                        notNullColumns);
                out.printf("    javaType=\"%s\" \n", ref.getParentTable().getJavaType());
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
        ColumnNaming cname = project.naming(column);
        // Class<?> type = column.getType();

        String property = cname.propertyName;
        if (property == null || ignoredProperties.contains(property))
            return;

        String tag = column.isPrimaryKey() ? "id" : "result";
        out.printf("<%s property=\"%s\" column=\"%s\" />\n", //
                tag, templates.toProperty(column), cname.column);
    }

    void sql_objlist_sql(XmlSourceBuffer out, ITableMetadata table, String id) {
        String qTableName = DialectFn.quoteQName(table.getCompactName());
        List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());
        out.printf("<sql id=\"%s\"><![CDATA[\n", id);
        out.enter();
        {
            out.println("select");

            out.enter();
            templates.sqlColumnNameList(out, columns, "a.");
            out.println();
            out.leave();

            out.println("from " + qTableName + " a");
            out.println("]]>");
            out.leave();
        }
        out.println("</sql>");
    }

    void sql_objedit_sql(XmlSourceBuffer out, ITableMetadata table, String id) {
        String qTableName = DialectFn.quoteQName(table.getCompactName());
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

            out.println("from " + qTableName + " a");

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
        // List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());

        out.println("<sql id=\"filtconds\">");
        out.enter();
        {
            out.println("${c.sqlCondition}");
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

    boolean select(XmlSourceBuffer out, ITableMetadata table, String sqlId) {
        IColumnMetadata[] keyCols = table.getPrimaryKeyColumns();
        if (keyCols.length == 0) {
            keyCols = getIdColumnsFromUsageInfo(table);
            if (keyCols == null) {
                IColumnMetadata idColumn = getDefaultSingleIdColumn(table);
                if (idColumn != null)
                    keyCols = new IColumnMetadata[] { idColumn };
                else
                    keyCols = new IColumnMetadata[0]; // { table.getColumn(0) };
            }
        }
        if (keyCols.length == 0)
            return false;

        out.println();
        out.println("<select id=\"select\" resultMap=\"objlist_map\">");
        out.enter();
        {
            out.printf("<include refid=\"%s\" />\n", sqlId);
            out.println("<where>");
            out.enter();
            {
                out.printf("<if test=\"_parameter != null\">");
                templates.sqlMatchPrimaryKey(out, "id", keyCols, "a");
                out.printf("</if>\n");
                out.leave();
            }
            out.println("</where>");
            out.leave();
        }
        out.println("</select>");
        return true;
    }

    IColumnMetadata[] getIdColumnsFromUsageInfo(ITableMetadata _view) {
        IViewMetadata view = (IViewMetadata) _view;
        L: for (ITableUsage tableUsage : view.getTableUsages()) {
            Set<String> columnUsage = new HashSet<>(tableUsage.getFromColumns());

            ITableMetadata parent = _view.getCatalog().getTable(tableUsage.getFromTableId());
            if (parent == null) {
                logger.warn("referenced table isn't loaded: " + tableUsage.getFromTableId());
                continue;
            }

            IColumnMetadata[] parentKeyCols = parent.getPrimaryKeyColumns();
            for (IColumnMetadata parentKeyCol : parentKeyCols) {
                String parentColName = parentKeyCol.getName();
                if (! columnUsage.contains(parentColName))
                    continue L;
            }

            // all primary key columns are included in the view.
            IColumnMetadata[] viewKeyCols = new IColumnMetadata[parentKeyCols.length];
            for (int i = 0; i < viewKeyCols.length; i++) {
                String name = parentKeyCols[i].getName();
                IColumnMetadata viewCol = _view.getColumn(name);
                viewKeyCols[i] = viewCol;
            }
            return viewKeyCols;
        }
        return null;
    }

    IColumnMetadata getDefaultSingleIdColumn(ITableMetadata table) {
        String[] idColumnNames = { "id", "uuid", "guid", };
        for (String name : idColumnNames) {
            IColumnMetadata column = table.getColumn(name);
            if (column != null)
                return column;
        }
        return null;
    }

    void select_count(XmlSourceBuffer out, ITableMetadata table) {
        String qTableName = DialectFn.quoteQName(table.getCompactName());

        out.println("<select id=\"count\" resultType=\"long\">");
        out.enter();
        {
            out.printf("select count(*) \"rows\" from %s a\n", //
                    qTableName);
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

}