package net.bodz.lily.tool.daogen.dir;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.entity.BeanIdentityTypeInfo;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;

public class Foo_IdTypeInfo__java
        extends DaoGen__java {

    public Foo_IdTypeInfo__java(DaoGenProject project) {
        super(project, project.Foo_IdTypeInfo);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata tableView) {
        ITableMetadata table = tableView;

        IColumnMetadata[] primaryKeyColumns = table.getPrimaryKeyColumns();
        switch (primaryKeyColumns.length) {
            case 0:
                throw new IllegalArgumentException("no primary key column.");
        }

        for (IColumnMetadata primaryKeyCol : primaryKeyColumns)
            if (primaryKeyCol.isExcluded())
                throw new IllegalUsageException("Can't exclude primary key column");

        out.printf("public class %s\n", project.Foo_IdTypeInfo.name);
        out.printf("        extends %s {\n", //
                out.im.name(BeanIdentityTypeInfo.class));
        out.enter();
        {
            out.println();
            ctor(out);

            out.println();
            singleton(out);

            out.leave();
        }
        out.println();
        out.println("}");
    }

    void ctor(ITreeOut out) {
        out.println("public " + project.Foo_IdTypeInfo.name + "() {");
        out.enter();
        out.printf("super(%s.class);\n", project.Foo_Id.name);
        out.leave();
        out.println("}");
    }

    void singleton(ITreeOut out) {
        //            public static final ExternalFooIdTypeInfo INSTANCE = new ExternalFooIdTypeInfo();
        out.printf("public static final %s INSTANCE = new %s();\n", //
                project.Foo_IdTypeInfo.name, project.Foo_IdTypeInfo.name);
    }

}
