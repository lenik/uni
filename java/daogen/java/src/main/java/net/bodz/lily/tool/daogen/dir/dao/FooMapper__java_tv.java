package net.bodz.lily.tool.daogen.dir.dao;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.db.ibatis.IEntityMapper;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;

public class FooMapper__java_tv
        extends JavaGen__java {

    public FooMapper__java_tv(JavaGenProject project) {
        super(project, project.FooMapper);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        out.println("public interface " + project.FooMapper.name);
        out.enter();
        {
            out.enter();
            {
                out.enterln("extends");
                out.printf("%s<%s> {\n", //
                        out.im.name(IEntityMapper.class), //
                        out.im.name(project.Foo.qName));
                out.leave();

                out.println();
                out.leave();
            }
            out.leave();
        }
        out.println("}");
    }

}
