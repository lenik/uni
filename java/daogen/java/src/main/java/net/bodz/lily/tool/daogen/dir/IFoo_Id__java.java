package net.bodz.lily.tool.daogen.dir;

import java.io.Serializable;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;
import net.bodz.lily.tool.daogen.OutFormat;

public class IFoo_Id__java
        extends DaoGen__java {

    public IFoo_Id__java(DaoGenProject project) {
        super(project, project.IFoo_Id);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        IColumnMetadata[] primaryKeyCols = table.getPrimaryKeyColumns();
        switch (primaryKeyCols.length) {
        case 0:
            throw new IllegalArgumentException("no primary key column.");
        }

        out.printf("public interface %s\n", project.IFoo_Id.name);
        out.println("        implements");
        out.println("            " + out.importName(Serializable.class) + " {");
        out.enter();
        {

            templates.FIELD_consts(out, table, true, OutFormat.JAVA);
            templates.N_consts(out, table, true, OutFormat.JAVA);
            templates.ord_consts(out, table, true, OutFormat.JAVA);

            for (IColumnMetadata column : primaryKeyCols) {
                out.println();
                templates.columnAccessors(out, column, false);
            }

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
