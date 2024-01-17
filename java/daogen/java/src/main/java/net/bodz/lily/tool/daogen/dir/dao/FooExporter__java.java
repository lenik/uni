package net.bodz.lily.tool.daogen.dir.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.db.sql.DDLExporter;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.ColumnName;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.tool.daogen.util.SBCodegen;

public class FooExporter__java
        extends JavaGen__java {

    public FooExporter__java(JavaGenProject project) {
        super(project, project.FooExporter);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.printf("public class %s\n", project.FooExporter.name);
        out.printf("        extends %s {\n", out.im.name(DDLExporter.class));
        out.enter();
        {

            out.println();

            mtdExportDDL(out, table);

            out.println();
            out.leave();
        }
        out.println();
        out.println("}");
    }

    protected void mtdExportDDL(JavaSourceWriter out, ITableMetadata table) {
        out.printf("public static String exportDDL(%s obj) {\n", //
                out.im.name(table.getJavaQName()));
        out.enter();
        {
            Map<String, String> map = new LinkedHashMap<>();
            for (IColumnMetadata column : table.getColumns()) {
                ColumnName cname = project.columnName(column);
                String col = cname.columnQuoted;
                String getExpr = templates.getJavaGetExpr(column);
                if (getExpr == null)
                    continue;
                String code = String.format("encode(obj.%s, %s)", //
                        getExpr, //
                        out.im.simpleId(column.getJdbcType()));
                map.put(col, code);
            }

            SBCodegen sb = new SBCodegen(out, "sb", null);

            sb.append("insert into %s(", table.getName());

            int i = 0;
            for (String col : map.keySet()) {
                if (i++ == 0)
                    sb.append(col);
                else
                    sb.append(", " + col);
            }

            sb.append(") values (");

            i = 0;
            for (String col : map.keySet()) {
                if (i++ != 0)
                    sb.append(", ");
                sb.appendCode(map.get(col));
            }

            sb.appendLine("); ");

            out.println("return sb.toString();");
            out.leave();
        }
        out.println("}");
    }

}
