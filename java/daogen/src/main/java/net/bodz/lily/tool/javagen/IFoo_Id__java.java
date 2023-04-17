package net.bodz.lily.tool.javagen;

import java.io.Serializable;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;

public class IFoo_Id__java
        extends JavaGen__java {

    public IFoo_Id__java(JavaGenProject project) {
        super(project, project.IFoo_Id);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata tableView) {
        ITableMetadata table = (ITableMetadata) tableView;

        IColumnMetadata[] primaryKeyCols = table.getPrimaryKeyColumns();
        switch (primaryKeyCols.length) {
        case 0:
            throw new IllegalArgumentException("no primary key column.");
        }

        out.printf("public interface %s\n", project.IFoo_Id.name);
        out.println("        implements");
        out.println("            " + out.im.name(Serializable.class) + " {");
        out.enter();
        {

            templates.N_consts(out, table, true);
            templates.O_consts(out, table, true);

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
