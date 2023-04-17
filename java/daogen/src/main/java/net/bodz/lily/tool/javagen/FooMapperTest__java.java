package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;

public class FooMapperTest__java
        extends JavaGen__java {

    public FooMapperTest__java(JavaGenProject project) {
        super(project, project.FooMapperTest);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata model) {
        out.println("public class " + project.FooMapperTest.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s, %s, %s> {\n", //
                        out.im.name("net.bodz.lily.test.AbstractTableTest"), //
                        out.im.name(project.Foo), //
                        out.im.name(project.FooMask), //
                        project.FooMapper.name);
                out.leave();
            }

            buildMapperMethods(out, model);

            out.println();
            out.leave();
        }
        out.println("}");
    }

    protected void buildMapperMethods(JavaSourceWriter out, ITableMetadata model) {
        out.println();
        out.println("@Override");
        out.printf("public %s buildSample() throws Exception {\n", //
                out.im.name(project.Foo));
        out.enter();
        {
            out.println("return " + out.im.name(project.FooSamples) + ".build();");
            out.leave();
        }
        out.println("}");
    }

}
