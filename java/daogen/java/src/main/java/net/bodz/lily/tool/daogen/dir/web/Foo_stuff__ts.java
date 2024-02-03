package net.bodz.lily.tool.daogen.dir.web;

import java.util.HashSet;
import java.util.Set;

import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TableType;

public class Foo_stuff__ts
        extends JavaGen__ts {

    Set<String> newLineProps = new HashSet<>();

    public Foo_stuff__ts(JavaGenProject project) {
        super(project, project.Esm_Foo_stuff);

        newLineProps.add("description");
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = EsmModules.local.source("./PersonValidators");
        out.im.add(validators.name("*", "validators"));

        String className = project.Esm_Foo_stuff.fullName;
        TableType tableType = new TableType(project, out, table, className);
        String typeName = tableType.simpleName + "Type";

        out.printf("export class %s extends %s {\n", //
                tableType.simpleName, //
                out.localName(QualifiedName.parse(tableType.baseClassName)) + tableType.baseParams);
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
