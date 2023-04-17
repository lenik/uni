package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.db.ibatis.IEntityMapper;
import net.bodz.bas.t.catalog.ITableMetadata;

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
                out.printf("%s<%s, %s> {\n", //
                        out.im.name(IEntityMapper.class), //
                        out.im.name(project.Foo), //
                        out.im.name(project.FooMask));
                out.leave();

                out.println();
                out.leave();
            }
            out.leave();
        }
        out.println("}");
    }

}
