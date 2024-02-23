package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Type;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanTypeProvider;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooValidators1__ts
        extends JavaGen__ts {

    public FooValidators1__ts(JavaGenProject project) {
        super(project, project.Esm_FooValidators);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project.Foo.qName);

        out.name(EsmModules.core.uiTypes.ValidateResult);

        QualifiedName className = project.Esm_FooValidators.qName;
        QualifiedName superType = project.Esm_Foo_stuff_Validators.qName;

        out.printf("export class %s extends %s {\n", //
                className.name, //
                out.importDefaultAs(superType));
        out.enter();

        int i = 0;

        if (extend.clazz != null) {
            IType type = BeanTypeProvider.getInstance().getType(extend.clazz);
            for (IProperty property : type.getProperties()) {
                if (property.getDeclaringClass() == extend.clazz) {
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

        String tsType = tsTypes.resolve(type, property.getName());

        out.printf("validate%s(val: %s) {\n", //
                Strings.ucfirst(property.getName()), //
                tsType);
        out.println("}");
    }

}
