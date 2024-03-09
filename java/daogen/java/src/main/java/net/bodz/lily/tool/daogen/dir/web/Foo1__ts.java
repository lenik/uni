package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.repr.form.validate.NotNull;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TsTemplates;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo1__ts
        extends JavaGen__ts {

    public Foo1__ts(JavaGenProject project) {
        super(project, project.Esm_Foo);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo javaExtend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, //
                        project.Foo.qName, //
                        project._Foo_stuff.qName);

        QualifiedName typeName = project.Esm_FooType.qName;

        out.printf("export class %s extends %s%s {\n", //
                javaExtend.type.name, //
                out.importDefault(javaExtend.baseType), //
                javaExtend.angledBaseTypeArgs());
        out.enter();
        {
            TsTemplates.lazyProp(out, "_typeInfo", "TYPE", out.importDefault(typeName));
            out.println();

            if (javaExtend.javaClass != null) {
                int i = 0;
                IType type = BeanTypeProvider.getInstance().getType(javaExtend.javaClass);
                for (IProperty property : type.getProperties()) {
                    Class<?> declaringClass = property.getDeclaringClass();
                    if (declaringClass == javaExtend.javaClass) {
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

        out.println();
        out.printf("export default %s;\n", javaExtend.type.name);
    }

    void declProperty(TypeScriptWriter out, IProperty property) {
        boolean aNotNull = property.getAnnotation(NotNull.class) != null;
        Class<?> propertyClass = property.getPropertyClass();
        Type type = property.getPropertyGenericType();
        if (type instanceof TypeVariable<?>)
            type = property.getPropertyClass();

        boolean notNull = propertyClass.isPrimitive() || aNotNull;

        String tsType = typeResolver()//
                .property(property.getName())//
                .resolveGeneric(type);

        out.print(property.getName());
        if (! notNull)
            out.print("?");
        out.print(": ");
        out.print(tsType);
        out.println();
    }

}
