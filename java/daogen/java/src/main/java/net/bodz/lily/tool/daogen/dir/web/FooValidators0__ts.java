package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.esm.skel01.Skel01Modules;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__ts;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooValidators0__ts
        extends DaoGen__ts {

    public FooValidators0__ts(DaoGenProject project) {
        super(project, project.Esm_Foo_stuff_Validators);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project._Foo_stuff.qName);

        out.name(Skel01Modules.core.uiTypes.ValidateResult);

        QualifiedName className = project.Esm_Foo_stuff_Validators.qName;
        QualifiedName superType = extend.baseType.nameAdd(project.validatorsSuffix);

        out.printf("export class %s extends %s {\n", //
                className.name, //
                out.importDefault(superType));
        out.enter();

        out.println();
        out.printf("constructor(type: %s) {\n", out.importDefaultType(project.Esm_Foo_stuff_Type.qName));
        out.enter();
        out.println("super(type);");
        out.leave();
        out.println("}");

        out.println();
        out.printf("get type() {\n");
        out.enter();
        out.printf("return this._type as %s;\n", out.importDefaultType(project.Esm_Foo_stuff_Type.qName));
        out.leave();
        out.println("}");

        for (IColumnMetadata column : table.getColumns()) {
            if (column.isExcluded())
                continue;

            if (column.isPropertyOfComposite()) {
//                checkCompositeProperty(table, column);
                continue;
            }

            if (column.isForeignKey())
                continue;

            out.println();
            validateProperty(out, column);
        }

        for (CrossReference xref : table.getForeignKeys().values()) {
            if (xref.isExcluded(table))
                continue;

            if (xref.isCompositeProperty())
                continue;

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
        String tsType = typeResolver().property(cname.propertyName)//
                .importAsType().resolve(javaType);

        out.printf("validate%s(val: %s) {\n", //
                cname.capPropertyName, //
                tsType);
        out.println("}");
    }

    void validateForeignKeyProperty(TypeScriptWriter out, CrossReference xref, ITableMetadata table) {
        String propertyName = xref.getPropertyName();
        QualifiedName javaType = xref.getParentTable().getJavaType();
        String tsType = typeResolver().importAsType().property(propertyName).resolve(javaType);

        out.printf("validate%s(val: %s) {\n", //
                Strings.ucfirst(propertyName), //
                tsType);
        out.println("}");
    }

}
