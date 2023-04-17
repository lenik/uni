package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;

public class FooMask__java
        extends JavaGen__java {

    public FooMask__java(JavaGenProject project) {
        super(project, project.FooMask);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.printf("public class %s\n", project.FooMask.name);
        out.printf("        extends %s {\n", out.im.name(project._FooMask_stuff));
        out.enter();
        {
//            out.println();
//            out.println("private static final long serialVersionUID = 1L;");
            out.leave();
        }
        out.println();
        out.println("}");
    }

}
