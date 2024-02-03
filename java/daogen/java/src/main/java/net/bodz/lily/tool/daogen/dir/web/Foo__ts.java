package net.bodz.lily.tool.daogen.dir.web;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;

import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.lily.concrete.IdEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.Attrs;

public class Foo__ts
        extends JavaGen__ts {

    Set<String> newLineProps = new HashSet<>();

    public Foo__ts(JavaGenProject project) {
        super(project, project.Esm_Foo);

        newLineProps.add("description");
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = EsmModules.local.source("./PersonValidators");
        out.im.add(validators.name("*", "validators"));

        TableOid oid = table.getId();

        QualifiedName idType = templates.getIdType(table);

        String simpleName = project.Foo.name;
        String baseClassName = table.getBaseTypeName();
        String baseParams = "";

        String description = table.getDescription();

        if (baseClassName == null) {
            if (idType != null) {
                baseClassName = IdEntity.class.getName();
                baseParams = "<" + idType + ">";
            } else
                baseClassName = StructRow.class.getName();
        }

        String typeName = simpleName + "Type";
        String superTypeName = baseClassName + "Type";

        out.println("// Type Info");
        out.println();
        out.printf("export class %s extends %s {\n", //
                typeName, //
                out.localName(QualifiedName.parse(superTypeName)));
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

                Attrs attrs = new Attrs(newLineProps);
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
                        typeName);
                out.leave();
            }
            out.println("}");
            out.println();
            out.leave();
        }
        out.println("}");
        out.println();
        out.println();
        out.printf("export class %s extends %s {\n", //
                simpleName, //
                out.localName(QualifiedName.parse(baseClassName)) + baseParams);
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
