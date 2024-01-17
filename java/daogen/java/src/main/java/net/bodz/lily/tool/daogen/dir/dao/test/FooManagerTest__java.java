package net.bodz.lily.tool.daogen.dir.dao.test;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.test.AbstractManagerTest;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;

public class FooManagerTest__java
        extends JavaGen__java {

    public FooManagerTest__java(JavaGenProject project) {
        super(project, project.FooManagerTest);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.println("public class " + project.FooManagerTest.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s, %s, %s> {\n", //
                        out.im.name(AbstractManagerTest.class), //
                        out.im.name(project.Foo.getFullName()), //
                        out.im.name(project.FooMapper.getFullName()), //
                        out.im.name(project.FooManager.getFullName()));
                out.leave();
            }

            mtdBuildSample(out, table);

            out.println();
            out.leave();
        }
        out.println("}");
    }

    protected void mtdBuildSample(JavaSourceWriter out, ITableMetadata table) {
        out.println();
        out.println("@Override");
        out.printf("public %s buildSample()\n", //
                out.im.name(project.Foo));
        out.println("        throws Exception {");
        out.enter();
        {
            out.printf("%s a = new %s();\n", //
                    out.im.name(project.FooSamples), //
                    out.im.name(project.FooSamples));

            out.println("return a.buildWired(tables);");
            out.leave();
        }
        out.println("}");
    }

}
