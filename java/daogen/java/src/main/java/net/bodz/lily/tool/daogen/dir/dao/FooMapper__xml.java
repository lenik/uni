package net.bodz.lily.tool.daogen.dir.dao;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.db.sql.dialect.DialectFn;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.ICatalogMetadata;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.IViewMetadata;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.ColumnUtils;
import net.bodz.lily.tool.daogen.DaoGenProject;

public class FooMapper__xml
        extends VFooMapper__xml {

    public FooMapper__xml(DaoGenProject project) {
        super(project);
    }

    @Override
    protected void buildXmlBody(XmlSourceBuffer out, ITableMetadata tableView) {
        ITableMetadata table = tableView;

        out.println("<!DOCTYPE mapper");
        out.println("PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
        out.println("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");

        out.println("<mapper namespace=\"" + project.FooMapper.qName + "\">");
        out.enter();
        {
            out.println();
            resultMap_objlist_map(out, table);
//            out.println();
//            resultMap_objedit_map(out, table);
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

            IColumnMetadata[] keyCols = table.getPrimaryKeyColumns();
            if (keyCols.length == 0) {
                if (table instanceof IViewMetadata) {
                    keyCols = getIdColumnsFromUsageInfo((IViewMetadata) table);
                    if (keyCols == null) {
                        IColumnMetadata idColumn = getDefaultSingleIdColumn(table);
                        if (idColumn != null)
                            keyCols = new IColumnMetadata[] { idColumn };
                        else
                            keyCols = new IColumnMetadata[0]; // { table.getColumn(0) };
                    }
                }
            }

            if (keyCols.length != 0) {
                out.println();
                select(out, table, SQLID_OBJEDIT, keyCols);
            }

            out.println();
            insert(out, table);

            if (keyCols.length != 0) {
                out.println();
                update(out, table, keyCols);

                out.println();
                delete(out, table, keyCols);
            }

            out.println();
            select_count(out, table);

            out.println();
            if (project.extraDDLs) {
                if (deleteXrefs(out, table))
                    out.println();
                if (createXrefs(out, table))
                    out.println();
            }

            out.leave();
        }
        out.println("</mapper>");
    }

    void insert(XmlSourceBuffer out, ITableMetadata table) {
        String qTableName = DialectFn.quoteQName(table.getCompactName());
        List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());

        IColumnMetadata[] primaryKeyColumns = table.getPrimaryKeyColumns();
        boolean useGeneratedKeys = false;
        List<String> keyColumns = new ArrayList<>();
        List<String> keyProperties = new ArrayList<>();
        for (IColumnMetadata column : primaryKeyColumns) {
            if (column.isAutoIncrement() || column.hasDefaultValue())
                useGeneratedKeys = true;
            ColumnNaming cname = project.naming(column);
            keyColumns.add(cname.columnQuoted);
            keyProperties.add(cname.propertyName);
        }

        out.printf("<insert id=\"insert\" useGeneratedKeys=\"%s\" keyProperty=\"%s\" keyColumn=\"%s\"><![CDATA[\n", //
                useGeneratedKeys, //
                StringArray.join(", ", keyProperties), //
                StringArray.join(", ", keyColumns).replace("\"", "&quot;"));
        out.enter();
        {
            out.printf("insert into %s(\n", qTableName);
            {
                out.enter();
                boolean first = true;

                for (IColumnMetadata column : columns) {
                    if (ColumnUtils.isIgnoredInCreation(column))
                        continue;

                    if (!first)
                        out.print(",\n");

                    ColumnNaming cname = project.naming(column);
                    out.print(cname.columnQuoted);

                    first = false;
                }
                out.leave();
                out.println();
            }

            out.enterln(") values(");
            {
                boolean first = true;
                for (IColumnMetadata column : columns) {
                    if (ColumnUtils.isIgnoredInCreation(column))
                        continue;

                    if (!first)
                        out.print(",\n");

                    out.print(templates.toSqlVar(null, column));

                    first = false;
                }
                out.leave();
                out.println();
            }

            out.println(");");
            out.leave();
        }
        out.println("]]></insert>");
    }

    void update(XmlSourceBuffer out, ITableMetadata table, IColumnMetadata[] keyCols) {
        String qTableName = DialectFn.quoteQName(table.getCompactName());
        List<IColumnMetadata> columns = getIncludedColumns(table.getColumns());

        out.enterln("<update id=\"update\">");
        {
            out.printf("update %s\n", qTableName);
            out.enterln("<set>");
            {
                boolean co = false;
                if (co)
                    out.println("<include refid=\"co.setUS\" />");
                for (IColumnMetadata column : columns) {
                    if (column.isPrimaryKey())
                        continue;
                    ColumnNaming cname = project.naming(column);
                    out.printf("%s = %s", cname.columnQuoted, templates.toSqlVar(null, column));
                    out.println(",");
                }
            }
            out.leaveln("</set>");

            out.enterln("<where>");
            templates.sqlMatchPrimaryKey(out, null, keyCols, null);
            out.println();
            out.leaveln("</where>");
        }
        out.leaveln("</update>");
    }

    void delete(XmlSourceBuffer out, ITableMetadata table, IColumnMetadata[] keyCols) {
        String qTableName = DialectFn.quoteQName(table.getCompactName());
        out.enterln("<delete id=\"delete\">");
        {
            out.println("delete from " + qTableName);

            out.enterln("<where>");
            templates.sqlMatchPrimaryKey(out, "id", keyCols, null);
            out.println();
            out.leaveln("</where>");

        }
        out.leaveln("</delete>");
    }

    boolean deleteXrefs(XmlSourceBuffer out, ITableMetadata table) {
        ICatalogMetadata catalog = table.getCatalog();
        List<CrossReference> xrefs = catalog.findCrossReferences(table.getId());
        if (xrefs.isEmpty())
            return false;

        out.enterln("<update id=\"delete_xrefs\">");
        {
            for (CrossReference xref : xrefs) {
                out.printf("alter table %s drop constraint %s;\n", //
                        DialectFn.quoteQName(xref.getForeignKey().getId()), //
                        DialectFn.quoteName(xref.getConstraintName()));
            }
        }
        out.leaveln("</update>");
        return true;
    }

    boolean createXrefs(XmlSourceBuffer out, ITableMetadata table) {
        ICatalogMetadata catalog = table.getCatalog();
        List<CrossReference> xrefs = catalog.findCrossReferences(table.getId());
        if (xrefs.isEmpty())
            return false;

        out.enterln("<update id=\"create_xrefs\">");
        {
            for (CrossReference xref : xrefs) {
                out.printf("alter table %s\n", //
                        DialectFn.quoteQName(xref.getForeignKey().getId()));
                out.enter();

                // add constraint _ foreign key (c1, c2, ...)
                out.printf("add constraint %s foreign key (", //
                        DialectFn.quoteName(xref.getConstraintName()));
                int i = 0;
                for (IColumnMetadata foreignColumn : xref.getForeignColumns()) {
                    if (i++ != 0)
                        out.print(", ");
                    out.print(DialectFn.quoteName(foreignColumn.getName()));
                }
                out.println(")");

                // references PARENT(p1, p2, ...)
                i = 0;
                out.printf("references %s (", //
                        DialectFn.quoteQName(xref.getParentKey().getId()));
                for (IColumnMetadata parentColumn : xref.getParentColumns()) {
                    if (i++ != 0)
                        out.print(", ");
                    out.print(DialectFn.quoteName(parentColumn.getName()));
                }
                out.println(")");

                // on update cascade
                switch (xref.getUpdateRule()) {
                    case DatabaseMetaData.importedKeyCascade:
                        out.println("on update cascade");
                        break;
                }

                // on delete cascade
                switch (xref.getDeleteRule()) {
                    case DatabaseMetaData.importedKeyCascade:
                        out.println("on delete cascade");
                        break;
                    case DatabaseMetaData.importedKeySetDefault:
                        out.println("on delete set default");
                        break;
                    case DatabaseMetaData.importedKeySetNull:
                        out.println("on delete set null");
                        break;
                }

                out.println(";");
                out.leave();
            }
        }
        out.leaveln("</update>");
        return true;
    }

}