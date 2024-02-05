package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo__ts
        extends JavaGen__ts {

    public Foo__ts(JavaGenProject project) {
        super(project, project.Esm_Foo);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = EsmModules.local.source("./PersonValidators");
        out.im.add(validators.name("*", "validators"));

        TypeExtendInfo extend = new TypeExtendInfo(project, out, table, project.Esm_Foo.qName);
        String simpleName = extend.simpleName;
        String typeName = simpleName + "Type";

        out.printf("export class %s extends %s {\n", //
                extend.simpleName, //
                extend.baseClassName + extend.baseParams);
        out.enter();
        {
            out.printf("static TYPE = new %s();\n", typeName);
            out.println();
            {
                out.println("gender?: string");
            }
            out.println();
            out.println("constructor(o: any) {");
            out.enter();
            {
                out.println("super(o);");
                out.println("if (o != null) Object.assign(this, o);");
                out.leave();
            }
            out.println("}");
            out.leave();
        }
        out.println("}");
    }

}