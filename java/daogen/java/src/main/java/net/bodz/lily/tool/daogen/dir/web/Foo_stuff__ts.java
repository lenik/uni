package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableKey;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo_stuff__ts
        extends JavaGen__ts {

    static final Logger logger = LoggerFactory.getLogger(Foo_stuff__ts.class);

    public Foo_stuff__ts(JavaGenProject project) {
        super(project, project.Esm_Foo_stuff);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out, true)//
                .getExtendInfo(table, project._Foo_stuff.qName);

        QualifiedName typeName = project.Esm_Foo_stuff_Type.qName;

        // out.localName(tableType.baseClassName);
        out.printf("export class %s%s extends %s%s {\n", //
                extend.simpleName, //
                extend.params, //
                out.importName(extend.baseClassName), //
                extend.baseParams);
        out.enter();
        {
            out.printf("static TYPE = new %s();\n", //
                    out.importName(typeName));
            out.println();

            for (IColumnMetadata column : table.getColumns()) {
                if (column.isExcluded())
                    continue;

                if (column.isCompositeProperty()) {
                    checkCompositeProperty(table, column);
                    continue;
                }

                if (column.isForeignKey())
                    continue;

                defineProperty(out, column);
            }

            for (CrossReference xref : table.getForeignKeys().values()) {
                if (xref.isExcluded(table))
                    continue;

                if (xref.isCompositeProperty())
                    continue;

                out.println();
                defineForeignKeyProperty(out, xref, table);

                for (String fkColumnName : xref.getForeignKey().getColumnNames()) {
                    IColumnMetadata column = table.getColumn(fkColumnName);
                    defineProperty(out, column);
                }
            }

            out.println();
            out.println("constructor(o: any) {");
            out.enter();
            {
                out.println("super(o);");
                // out.println("if (o != null) Object.assign(this, o);");
                out.leave();
            }
            out.println("}");
            out.leave();
        }
        out.println("}");
    }

    void checkCompositeProperty(ITableMetadata table, IColumnMetadata column) {
        ColumnNaming cname = project.naming(column);

        // composite property, need to be declared in the user type.
        // check if exists:
        String head = Split.headDomain(cname.propertyName).a;
        IProperty headProperty = table.getEntityType().getProperty(head);
        if (headProperty == null)
            logger.warnf("context property (%s.%s) of the composite property(%s) isn't defined.",
                    table.getEntityTypeName(), head, cname.propertyName);
    }

    void defineProperty(TypeScriptWriter out, IColumnMetadata column) {
        ColumnNaming cname = project.naming(column);
        out.print(cname.propertyName);

        if (column.isNullable(true))
            out.print("?");
        out.print(": ");

        String javaType = project.config.javaType(column);
        // String simpleType = Split.packageName(javaType).b;
        String tsType = tsTypes.resolve(javaType);

        if (tsType.contains("."))
            tsType = out.importName(tsType);
        EsmName esmName = TsUtils.getAlias(tsType);
        if (esmName != null)
            out.name(esmName);

        out.print(tsType);
        // default = ..
        out.println(";");
    }

    public void defineForeignKeyProperty(TypeScriptWriter out, CrossReference xref, ITableMetadata table) {
        TableKey foreignKey = xref.getForeignKey();
        IColumnMetadata[] columns = foreignKey.resolve(table);
        TableOid parentOid = xref.getParentKey().getId();
        ITableMetadata parentTable = project.catalog.getTable(parentOid);
        if (parentTable == null) {
            throw new UnexpectedException("parent is not defined: " + parentOid);
        }

        boolean anyNotNull = false;
        for (IColumnMetadata c : columns)
            if (! c.isNullable(false)) {
                anyNotNull = true;
                break;
            }

        QualifiedName parentType = parentTable.getJavaType();
        if (parentType == null)
            throw new NullPointerException("parentType");

        String property = xref.getJavaName();

        out.print(property);
        if (! anyNotNull)
            out.print("?");
        out.print(": ");

        String tsType = out.importName(parentType);
        out.print(tsType);
        out.println(";");
    }

}
