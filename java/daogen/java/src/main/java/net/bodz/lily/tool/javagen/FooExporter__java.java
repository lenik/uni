package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.db.sql.DDLExporter;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;

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

            out.printf("public static String exportDDL(%s obj) {\n", out.im.name(table.getJavaQName()));
            out.enter();
            {
                out.printf("StringBuilder sb= new StringBuilder();");
                out.printf("sb.append(\"insert into %s(\");\n", table.getName());

                int i = 0;
                for (IColumnMetadata column : table.getColumns()) {
                    ColumnName cname = project.columnName(column);
                    out.printf("sb.append(\"" //
                            + (i == 0 ? "" : ", ") //
                            + cname.columnQuoted + "\");\n");
                    i++;
                }

                out.printf("sb.append(\") values (\");\n");

                i = 0;
                for (IColumnMetadata column : table.getColumns()) {
                    ColumnName cname = project.columnName(column);
                    if (i != 0)
                        out.println("sb.append(\", \");");
                    out.printf("sb.append(encode(obj.%s, %s));\n", //
                            cname.field, //
                            out.im.simpleId(column.getJdbcType()));
                    i++;
                }

                out.printf("sb.append(\"); \"); \n");
                out.println("return sb.toString();");
                out.leave();
            }
            out.println("}"); // end of function

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
