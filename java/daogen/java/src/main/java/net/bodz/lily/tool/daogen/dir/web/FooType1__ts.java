package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.repr.form.meta.TextInput;
import net.bodz.bas.repr.form.validate.NotNull;
import net.bodz.bas.repr.form.validate.Precision;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.Attrs;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooType1__ts
        extends JavaGen__ts {

    public FooType1__ts(JavaGenProject project) {
        super(project, project.Esm_FooType);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, //
                        project.Foo.qName, //
                        project._Foo_stuff.qName);

        String tsName = project.Esm_FooType.name;
        QualifiedName superType = project.Esm_Foo_stuff_Type.qName;
        QualifiedName validatorsClass = project.Esm_FooValidators.qName;

        String entityDescription = table.getDescription();

        out.printf("export class %s extends %s {\n", //
                tsName, //
                out.importDefault(superType));
        out.enter();
        {
            IType entityType = table.getPotatoType();
            String entityIcon = "fa-tag";
            String entityLabel = entityType.getLabel().toString();
            if (Nullables.isEmpty(entityDescription))
                entityDescription = entityType.getDescription().toString();

            out.println();
            out.printf("readonly validators = new %s(this);\n", //
                    out.importDefault(validatorsClass));

            out.println();
            out.printf("constructor(%s) {\n", extend.getCtorParams(this));
            {
                out.enter();
                // extend.baseParams;
                out.printf("super(%s);\n", extend.getSuperCtorArgs(this));
                out.leave();
            }
            out.println("}");

            out.println();
            out.printf("get name() { return \"%s\"; }\n", table.getJavaType());
            if (entityIcon != null)
                out.printf("get icon() { return \"%s\"; }\n", entityIcon);
            if (entityLabel != null)
                out.printf("get label() { return \"%s\"; }\n", entityLabel);
            if (entityDescription != null)
                out.printf("get description() { return \"%s\"; }\n", entityDescription);

            if (! Modifier.isAbstract(extend.javaClass.getModifiers())) {
                out.println();
                out.println("override create() {");
                {
                    out.enter();
                    out.printf("return new %s();\n", //
                            out.importDefault(extend.javaClass));
                    out.leave();
                }
                out.println("}");
            }

            out.println();
            out.println("override preamble() {");
            {
                out.enter();
                out.println("super.preamble();");
                out.println("this.declare({");
                {
                    out.enter();
                    if (extend.javaClass != null)
                        declareProps(out, extend.javaClass);
                    out.leave();
                }
                out.println("});");
                out.leave();
            }
            out.println("}");

            if (extend.typeVarCount() == 0) {
                out.println();
                out.printf("static readonly INSTANCE = new %s();\n", //
                        tsName);
            }

            out.println();
            out.leave();
        }
        out.println("}");

        out.println();
        out.printf("export default %s;\n", tsName);

        out.println();
        out.printf("export const %s = %s.INSTANCE;\n", //
                extend.type.name + "_TYPE", //
                tsName);
    }

    void declareProps(TypeScriptWriter out, Class<?> clazz) {
        IType type = BeanTypeProvider.getInstance().getType(clazz);
        for (IProperty property : type.getProperties()) {
            if (property.getDeclaringClass() == clazz) {
                declProperty(out, property);
            }
        }
    }

    void declProperty(TypeScriptWriter out, IProperty property) {
        boolean aNotNull = property.getAnnotation(NotNull.class) != null;
        Class<?> clazz = property.getPropertyClass();
        boolean notNull = clazz.isPrimitive() || aNotNull;

        Type type = property.getPropertyGenericType();
        if (type == null)
            throw new IllegalArgumentException("property's generic type is null: " + property);
        if (type instanceof TypeVariable<?>)
            type = property.getPropertyClass();

        String tsTypeInfo = typeInfoResolver().property(property.getName()).resolveGeneric(type);

        String label = property.getLabel().toString();
        String description = property.getDescription().toString();
        String icon = property.getXjdoc().getTextTag("icon").toString();

        out.print(property.getName());
        out.print(": ");
        out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsCodeStyle.newLineProps);
        attrs.put("type", tsTypeInfo);
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

        String validatorFn = String.format("this.validators.validate%s", //
                ColumnNaming.capitalize(property.getName()));
        attrs.put("validator", validatorFn);

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

}
