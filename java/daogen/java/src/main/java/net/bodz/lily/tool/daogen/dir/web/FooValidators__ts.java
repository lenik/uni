package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;

public class FooValidators__ts
        extends JavaGen__ts {

    public FooValidators__ts(JavaGenProject project) {
        super(project, project.Esm_FooValidators);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        out.name(EsmModules.core.uiTypes.ValidateResult);

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
    }

    void validateProperty(TypeScriptWriter out, IColumnMetadata column) {
        ColumnNaming cname = project.config.naming(column);

        String javaType = project.config.javaType(column);
        // String simpleType = Split.packageName(javaType).b;
        String tsType = tsTypes.resolve(javaType);
        if (tsType.contains("."))
            tsType = out.importName(tsType);

        out.printf("export function validate_%s(val: %s) {\n", //
                cname.propertyName, //
                tsType);
        out.println("}");
    }

    void validateForeignKeyProperty(TypeScriptWriter out, CrossReference xref, ITableMetadata table) {
        Class<?> type = xref.getParentTable().getEntityClass();
        String tsType = tsTypes.resolve(type);

        out.printf("export function validate_%s(val: %s) {\n", //
                xref.getJavaName(), //
                tsType);
        out.println("}");
    }
}
