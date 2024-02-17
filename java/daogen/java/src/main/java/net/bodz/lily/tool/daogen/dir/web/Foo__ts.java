package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Type;

import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.repr.form.validate.NotNull;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo__ts
        extends JavaGen__ts {

    public Foo__ts(JavaGenProject project) {
        super(project, project.Esm_Foo);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = out.findSource(project.Esm_FooValidators.qName, null);
        out.im.add(validators.wildcardAs("validators"));

        TypeExtendInfo javaExtend = new TypeAnalyzer(project, out, true)//
                .getExtendInfo(table, //
                        project.Foo.qName, //
                        project._Foo_stuff.qName);

        QualifiedName typeName = javaExtend.baseClassName.nameAdd("_Type");

        out.printf("export class %s extends %s%s {\n", //
                javaExtend.simpleName, //
                out.importName(javaExtend.baseClassName), //
                javaExtend.baseParams);
        out.enter();
        {
            out.printf("static TYPE = new %s();\n", //
                    out.importName(typeName));

            if (javaExtend.clazz != null) {
                int i = 0;
                IType type = BeanTypeProvider.getInstance().getType(javaExtend.clazz);
                for (IProperty property : type.getProperties()) {
                    Class<?> declaringClass = property.getDeclaringClass();
                    if (declaringClass == javaExtend.clazz) {
                        if (i++ == 0)
                            out.println();
                        declProperty(out, property);
                    }
                }
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

    void declProperty(TypeScriptWriter out, IProperty property) {
        boolean aNotNull = property.getAnnotation(NotNull.class) != null;
        Class<?> propertyClass = property.getPropertyClass();
        Type type = property.getPropertyGenericType();

        boolean notNull = propertyClass.isPrimitive() || aNotNull;

        String tsType = tsTypes.resolve(type);

        out.print(property.getName());
        if (!notNull)
            out.print("?");
        out.print(": ");
        out.print(out.importName(tsType));
        out.println();
    }

}
