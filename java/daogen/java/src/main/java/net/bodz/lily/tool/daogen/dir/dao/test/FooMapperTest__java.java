package net.bodz.lily.tool.daogen.dir.dao.test;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.test.AbstractTableTest;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;

public class FooMapperTest__java
        extends JavaGen__java {

    public FooMapperTest__java(JavaGenProject project) {
        super(project, project.FooMapperTest);
    }

    @Override
    protected boolean isTest() {
        return true;
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.println("public class " + project.FooMapperTest.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s, %s> {\n", //
                        out.im.name(AbstractTableTest.class), //
                        out.im.name(project.Foo.qName), //
                        project.FooMapper.name);
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
                out.im.name(project.Foo.qName));
        out.println("        throws Exception {");
        out.enter();
        {
            out.printf("%s a = new %s();\n", //
                    out.im.name(project.FooSamples.qName), //
                    out.im.name(project.FooSamples.qName));

            out.println("return a.buildWired(tables);");
            out.leave();
        }
        out.println("}");
    }

}
