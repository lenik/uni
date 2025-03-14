package net.bodz.lily.tool.daogen.dir.dao;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;

public class FooCriteriaBuilder__java
        extends DaoGen__java {

    public FooCriteriaBuilder__java(DaoGenProject project) {
        super(project, project.FooCriteriaBuilder);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.printf("public class %s\n", project.FooCriteriaBuilder.name);
        out.printf("        extends %s<%s> {\n", //
                out.im.name(project._FooCriteriaBuilder_stuff.qName), //
                project.FooCriteriaBuilder.name);

        out.println();

        out.println("}");
    }

}
