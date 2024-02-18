package net.bodz.lily.tool.daogen.dir.web;

import org.apache.logging.log4j.util.Strings;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.repr.form.meta.TextInput;
import net.bodz.bas.repr.form.validate.NotNull;
import net.bodz.bas.repr.form.validate.Precision;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.Attrs;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooType__ts
        extends JavaGen__ts {

    public FooType__ts(JavaGenProject project) {
        super(project, project.Esm_FooType);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = EsmModules.local.source("./PersonValidators");
        out.im.add(validators.name("*", "validators"));

        TypeExtendInfo javaExtend = new TypeAnalyzer(project, out, true)//
                .getExtendInfo(table, //
                        project.Foo.qName, //
                        project._Foo_stuff.qName);

        String tsName = project.Esm_FooType.name;
        QualifiedName baseType = project.Esm_Foo_stuff_Type.qName;

        String entityDescription = table.getDescription();

        out.println("// Type Info");
        out.println();
        out.printf("export class %s extends %s {\n", //
                tsName, //
                out.importName(baseType));
        out.println();
        out.enter();
        {
            IType entityType = table.getEntityType();
            String entityIcon = "fa-tag";
            String entityLabel = entityType.getLabel().toString();
            if (Strings.isEmpty(entityDescription))
                entityDescription = entityType.getDescription().toString();

            out.printf("name = \"%s\"\n", table.getEntityTypeName());
            if (entityIcon != null)
                out.printf("icon = \"%s\"\n", entityIcon);
            if (entityLabel != null)
                out.printf("label = \"%s\"\n", entityLabel);
            if (entityDescription != null)
                out.printf("description = \"%s\"\n", entityDescription);

            out.println();
            out.printf("static declaredProperty: %s = {\n", //
                    out.im.name(EsmModules.dba.entity.EntityPropertyMap));
            out.enter();
            {
                if (javaExtend.clazz != null) {
                    IType type = BeanTypeProvider.getInstance().getType(javaExtend.clazz);
                    for (IProperty property : type.getProperties()) {
                        if (property.getDeclaringClass() == javaExtend.clazz) {
                            declProperty(out, property);
                        }
                    }
                }
                out.leave();
            }
            out.println("}");
            out.println();
            out.println("constructor() {");
            out.enter();
            {
                out.println("super();");
                out.printf("this.declare(%s.declaredProperty);\n", //
                        tsName);
                out.leave();
            }
            out.println("}");
            out.println();
            out.leave();
        }
        out.println("}");
    }

    void declProperty(TypeScriptWriter out, IProperty property) {
        boolean aNotNull = property.getAnnotation(NotNull.class) != null;
        Class<?> type = property.getPropertyType().getJavaClass();
        boolean notNull = type.isPrimitive() || aNotNull;

        String tsType = tsTypes.resolve(type);

        String label = property.getLabel().toString();
        String description = property.getDescription().toString();
        String icon = property.getXjdoc().getTextTag("icon").toString();

        out.print(property.getName());
        out.print(": ");
        out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsConfig.newLineProps);
        attrs.putQuoted("type", tsType);
        if (notNull)
            attrs.put("nullable", false);

        int precision = 0;
        int scale = 0;

        TextInput aTextInput = property.getAnnotation(TextInput.class);
        if (aTextInput != null)
            precision = aTextInput.maxLength();

        Precision aPrecision = property.getAnnotation(Precision.class);
        if (aPrecision != null) {
            precision = aPrecision.value();
            scale = aPrecision.scale();
        }

        if (precision > 0 && precision < Integer.MAX_VALUE)
            attrs.put("precision", precision);

        if (scale > 0 && scale < Integer.MAX_VALUE)
            attrs.put("scale", scale);

        if (label != null)
            attrs.putQuoted("label", label);
        if (icon != null)
            attrs.putQuoted("icon", icon);
        if (description != null)
            attrs.putQuoted("description", description);

        attrs.put("validator", "validators.validate_" + property.getName());

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

}
