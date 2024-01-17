package net.bodz.lily.tool.daogen.dir.ws;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.meta.decl.ObjectType;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;

public class FooIndex__java
        extends JavaGen__java {

    public FooIndex__java(JavaGenProject project) {
        super(project, project.FooIndex);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.println("/**");
        out.println("* @label " + project.Foo.name);
        out.println("*/");
        out.printf("@%s(%s.class)\n", //
                out.im.name(ObjectType.class), //
                out.im.name(project.Foo.name));
        out.println("public class " + project.FooIndex.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s> {\n", //
                        out.im.name("net.bodz.lily.model.base.CoIndex"), //
                        out.im.name(project.Foo));
                out.leave();
            }
//            out.println();
//            out.println("public static final String SCHEMA = \"" + table.getName() + "\";");
            out.println();
            out.println("public " + project.FooIndex.name + "() {");
            out.enter();
            {
//                out.println("super(SCHEMA);");
                out.leave();
            }
            out.println("}");
            out.println();
            out.leave();
        }
        out.println("}");
    }

}
