package net.bodz.lily.tool.daogen.dir.dao.test;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;

public class FooMapperTest__java_v
        extends DaoGen__java {

    public FooMapperTest__java_v(DaoGenProject project) {
        super(project, project.FooMapperTest);
    }

    @Override
    protected boolean isTestScoped() {
        return true;
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata model) {
        out.println("public class " + project.FooMapperTest.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s, %s> {\n", //
                        out.im.name("net.bodz.lily.test.AbstractViewTest"), //
                        out.im.name(project.Foo.qName), //
                        project.FooMapper.name);
                out.leave();
            }

            methods(out, model);

            out.println();
            out.leave();
        }
        out.println("}");
    }

    protected void methods(JavaSourceWriter out, ITableMetadata table) {
    }

}
