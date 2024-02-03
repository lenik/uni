package net.bodz.lily.tool.daogen.dir.web;

import org.apache.logging.log4j.util.Strings;

import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.Attrs;
import net.bodz.lily.tool.daogen.util.TableType;

public class FooType__ts
        extends JavaGen__ts {

    public FooType__ts(JavaGenProject project) {
        super(project, project.Esm_FooType);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = EsmModules.local.source("./PersonValidators");
        out.im.add(validators.name("*", "validators"));

        TableType tableType = new TableType(project, out, table, project.Esm_FooType.fullName);
        String simpleName = tableType.simpleName;

        String description = table.getDescription();

        String baseClassTypeName = tableType.baseClassName + "Type";

        out.println("// Type Info");
        out.println();
        out.printf("export class %s extends %s {\n", //
                simpleName, //
                out.localName(QualifiedName.parse(baseClassTypeName)));
        out.println();
        out.enter();
        {
            IType type = table.getEntityType();
            String iconName = "fa-tag";
            String label = type.getLabel().toString();
            if (Strings.isEmpty(description))
                description = type.getDescription().toString();

            out.printf("name = \"%s\"\n", table.getEntityTypeName());
            if (iconName != null)
                out.printf("icon = \"%s\"\n", iconName);
            if (label != null)
                out.printf("label = \"%s\"\n", label);
            if (description != null)
                out.printf("description = \"%s\"\n", description);

            out.println();
            out.printf("static declaredProperty: %s = {\n", //
                    out.im.name(EsmModules.dba.entity.EntityPropertyMap));
            out.enter();
            {

                boolean primaryKey = false;

                String propName = "gender";
                out.print(propName);
                out.print(": ");
                if (primaryKey)
                    out.print(out.im.name(EsmModules.dba.entity.primaryKey));
                else
                    out.print(out.im.name(EsmModules.dba.entity.property));

                Attrs attrs = new Attrs(TsConfig.newLineProps);
                attrs.put("type", "string");
                attrs.put("icon", "fa-user");
                attrs.put("label", "label");
                attrs.put("description", "description");

                out.print("(");
                attrs.toJson(out, true);
                out.println("),");

                out.println();
                out.leave();
            }
            out.println("}");
            out.println();
            out.println("constructor() {");
            out.enter();
            {
                out.println("super();");
                out.printf("this.declare(%s.declaredProperty);\n", //
                        simpleName);
                out.leave();
            }
            out.println("}");
            out.println();
            out.leave();
        }
        out.println("}");
    }

}
