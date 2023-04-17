package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;

public class FooMapper__xml
        extends VFooMapper__xml {

    public FooMapper__xml(JavaGenProject project) {
        super(project);
    }

    @Override
    protected void buildXmlBody(XmlSourceBuffer out, ITableMetadata tableView) {
        ITableMetadata table = tableView;

        out.println("<!DOCTYPE mapper");
        out.println("PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
        out.println("\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");

        out.println("<mapper namespace=\"" + project.FooMapper + "\">");
        out.enter();
        {
            out.println();
            resultMap_objlist_map(out, table);
            out.println();
            sql_objlist_sql(out, table);
            out.println();
            sql_objedit_sql(out, table);
            out.println();
            sql_filtconds(out, table);
            out.println();
            select_all(out, table);
            out.println();
            select_filter(out, table);
            out.println();
            select(out, table);
            out.println();
            insert(out, table);
            out.println();
            update(out, table);
            out.println();
            delete(out, table);
            out.println();
            select_count(out, table);
            out.println();
            out.leave();
        }
        out.println("</mapper>");
    }

    void insert(XmlSourceBuffer out, ITableMetadata table) {
        out.printf("<insert id=\"insert\" useGeneratedKeys=\"true\" keyProperty=\"id\"><![CDATA[\n");
        out.enter();
        {
            out.printf("insert into %s(\n", table.nam().foo_bar);
            {
                out.enter();
                boolean first = true;

                for (IColumnMetadata column : table.getColumns()) {
                    if (ColumnUtils.isIgnoredInCreation(column))
                        continue;

                    if (!first)
                        out.print(",\n");

                    ColumnName cname = project.columnName(column);
                    out.print(cname.columnQuoted);

                    first = false;
                }
                out.leave();
                out.println();
            }

            out.enterln(") values(");
            {
                boolean first = true;
                for (IColumnMetadata column : table.getColumns()) {
                    if (ColumnUtils.isIgnoredInCreation(column))
                        continue;

                    if (!first)
                        out.print(",\n");

                    ColumnName cname = project.columnName(column);
                    out.print("#{" + cname.property + "}");

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

    void update(XmlSourceBuffer out, ITableMetadata table) {
        out.enterln("<update id=\"update\">");
        {
            out.printf("update %s\n", table.getCompactName());
            out.enterln("<set>");
            {
                boolean co = false;
                if (co)
                    out.println("<include refid=\"co.setUS\" />");
                for (IColumnMetadata column : table.getColumns()) {
                    if (column.isPrimaryKey())
                        continue;
                    ColumnName cname = project.columnName(column);
                    out.printf("%s = #{%s}", cname.columnQuoted, cname.property);
                    out.println(",");
                }
            }
            out.leaveln("</set>");

            out.enterln("<where>");
            templates.sqlMatchPrimaryKey(out, table.getPrimaryKeyColumns());
            out.leaveln("</where>");
        }
        out.leaveln("</update>");
    }

    void delete(XmlSourceBuffer out, ITableMetadata table) {
        out.enterln("<delete id=\"delete\">");
        {
            out.println("delete from " + table.getCompactName());

            out.enterln("<where>");
            templates.sqlMatchPrimaryKey(out, table.getPrimaryKeyColumns());
            out.leaveln("</where>");

        }
        out.leaveln("</delete>");
    }

}