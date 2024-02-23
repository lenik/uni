package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooValidators0__ts
        extends JavaGen__ts {

    public FooValidators0__ts(JavaGenProject project) {
        super(project, project.Esm_Foo_stuff_Validators);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project._Foo_stuff.qName);

        out.name(EsmModules.core.uiTypes.ValidateResult);

        QualifiedName className = project.Esm_Foo_stuff_Validators.qName;
        QualifiedName superType = extend.baseClassName.nameAdd("Validators");

        out.printf("export class %s extends %s {\n", //
                className.name, //
                out.importDefaultAs(superType));
        out.enter();

        int i = 0;

        for (IColumnMetadata column : table.getColumns()) {
            if (column.isExcluded())
                continue;

            if (column.isCompositeProperty()) {
//                checkCompositeProperty(table, column);
                continue;
            }

            if (column.isForeignKey())
                continue;

            if (i++ != 0)
                out.println();
            validateProperty(out, column);
        }

        for (CrossReference xref : table.getForeignKeys().values()) {
            if (xref.isExcluded(table))
                continue;

            if (xref.isCompositeProperty())
                continue;

            if (i++ != 0)
                out.println();
            validateForeignKeyProperty(out, xref, table);
        }

        out.leave();
        out.println();
        out.println("}");

        out.println();
        out.printf("export default %s;\n", className.name);
    }

    void validateProperty(TypeScriptWriter out, IColumnMetadata column) {
        ColumnNaming cname = project.config.naming(column);

        String javaType = project.config.javaType(column);
        String tsType = tsTypes.resolve(javaType, cname.propertyName);

        out.printf("validate%s(val: %s) {\n", //
                cname.ucfirstPropertyName, //
                tsType);
        out.println("}");
    }

    void validateForeignKeyProperty(TypeScriptWriter out, CrossReference xref, ITableMetadata table) {
        String propertyName = xref.getJavaName();
        Class<?> type = xref.getParentTable().getEntityClass();
        String tsType = tsTypes.resolve(type, propertyName);
        out.printf("validate%s(val: %s) {\n", //
                Strings.ucfirst(propertyName), //
                tsType);
        out.println("}");
    }

}
