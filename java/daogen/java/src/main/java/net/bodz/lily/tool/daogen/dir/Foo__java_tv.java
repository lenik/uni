package net.bodz.lily.tool.daogen.dir;

import javax.persistence.Table;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo__java_tv
        extends JavaGen__java {

    public Foo__java_tv(JavaGenProject project) {
        super(project, project.Foo);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        TableOid oid = table.getId();

        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project.Foo.qName, project._Foo_stuff.qName);

        String simpleName = extend.simpleName;

        out.print("@" + out.im.name(Table.class) + "(");
        {
            String catalog_name = oid.getCatalogName();
            if (catalog_name != null)
                out.printf("catalog = %s.CATALOG_NAME, ", simpleName);
            String schema_name = oid.getSchemaName();
            if (schema_name != null)
                out.printf("schema = %s.SCHEMA_NAME, ", simpleName);
            out.printf("name = %s.TABLE_NAME", simpleName);
            out.println(")");
        }

        out.printf("public class %s%s\n", simpleName, extend.params);
        out.printf("        extends %s%s {\n", out.importName(extend.baseClassName), extend.baseParams);
        out.enter();
        {
            out.println();
            out.println("private static final long serialVersionUID = 1L;");

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
