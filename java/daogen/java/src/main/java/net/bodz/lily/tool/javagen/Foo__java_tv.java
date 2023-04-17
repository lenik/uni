package net.bodz.lily.tool.javagen;

import javax.persistence.Table;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;

public class Foo__java_tv
        extends JavaGen__java {

    public Foo__java_tv(JavaGenProject project) {
        super(project, project.Foo);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        TableOid oid = table.getId();

        out.print("@" + out.im.name(Table.class) + "(");
        {
            String catalog_name = oid.getCatalogName();
            if (catalog_name != null)
                out.print("catalog=\"" + catalog_name + "\", ");
            String schema_name = oid.getSchemaName();
            if (schema_name != null)
                out.print("schema=\"" + schema_name + "\", ");
            out.print("name=\"" + oid.getTableName() + "\"");
            out.println(")");
        }

        out.printf("public class %s\n", project.Foo.name);
        out.printf("        extends %s {\n", out.im.name(project._Foo_stuff));
        out.enter();
        {
            out.println();
            out.println("private static final long serialVersionUID = 1L;");

            //

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
