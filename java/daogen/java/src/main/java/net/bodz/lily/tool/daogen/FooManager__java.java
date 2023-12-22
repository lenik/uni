package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.db.ctx.DataContext;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.dao.AbstractEntityManager;

public class FooManager__java
        extends JavaGen__java {

    public FooManager__java(JavaGenProject project) {
        super(project, project.FooManager);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.println("public class " + project.FooManager.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s, %s, %s> {\n", //
                        out.im.name(AbstractEntityManager.class), //
                        out.im.name(project.Foo), //
                        out.im.name(project.FooMask), //
                        out.im.name(project.FooMapper));
                out.leave();
            }

            out.println();
            out.printf("public %s(%s dataContext) {\n", //
                    project.FooManager.name, //
                    out.im.name(DataContext.class));
            out.enter();
            {
                out.printf("super(dataContext, %s.class);\n", //
                        out.im.name(project.FooMapper));
                out.leave();
            }
            out.println("}");

            methods(out, table);

            out.println();
            out.leave();
        }
        out.println("}");
    }

    protected void methods(JavaSourceWriter out, ITableMetadata table) {
    }

}
