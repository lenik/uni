package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.esm.skel01.Skel01Modules;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__ts;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooValidators1__ts
        extends DaoGen__ts {

    public FooValidators1__ts(DaoGenProject project) {
        super(project, project.Esm_FooValidators);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project.Foo.qName);

        out.name(Skel01Modules.core.uiTypes.ValidateResult);

        QualifiedName className = project.Esm_FooValidators.qName;
        QualifiedName superType = project.Esm_Foo_stuff_Validators.qName;

        out.printf("export class %s extends %s {\n", //
                className.name, //
                out.importDefault(superType));
        out.enter();

        out.println();
        out.printf("constructor(type: %s) {\n", out.importDefaultType(project.Esm_FooType.qName));
        out.enter();
        out.println("super(type);");
        out.leave();
        out.println("}");

        out.println();
        out.printf("get type() {\n");
        out.enter();
        out.printf("return this._type as %s;\n", out.importDefaultType(project.Esm_FooType.qName));
        out.leave();
        out.println("}");

        if (extend.javaClass != null) {
            IType type = BeanTypeProvider.getInstance().getType(extend.javaClass);
            for (IProperty property : type.getProperties()) {
                if (property.getDeclaringClass() == extend.javaClass) {
                    out.println();
                    validateProperty(out, property);
                }
            }
        }

        out.leave();
        out.println();
        out.println("}");

        out.println();
        out.printf("export default %s;\n", className.name);
    }

    void validateProperty(TypeScriptWriter out, IProperty property) {
        Type type = property.getPropertyGenericType();
        if (type instanceof TypeVariable<?>)
            type = property.getPropertyClass();

        String tsType = typeResolver().property(property.getName())//
                .importAsType().resolveGeneric(type);

        out.printf("validate%s(val: %s) {\n", //
                Strings.ucfirst(property.getName()), //
                tsType);
        out.println("}");
    }

}
