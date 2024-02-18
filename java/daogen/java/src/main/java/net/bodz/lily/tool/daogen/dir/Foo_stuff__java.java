package net.bodz.lily.tool.daogen.dir;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.entity.IdType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.tool.daogen.OutFormat;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo_stuff__java
        extends JavaGen__java {

    static final Logger logger = LoggerFactory.getLogger(Foo_stuff__java.class);

    public Foo_stuff__java(JavaGenProject project) {
        super(project, project._Foo_stuff);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project._Foo_stuff.qName);

        QualifiedName idType = extend.idType;

        String description = table.getDescription();
        if (description != null)
            templates.javaDoc(out, description);

        if (extend.typeAgain != null)
            out.printf("@%s({ %s })\n", //
                    out.importName(TypeParameters.class), //
                    extend.typeAgain);

        if (extend.idType != null)
            out.printf("@%s(%s.class)\n", //
                    out.importName(IdType.class), //
                    out.importName(idType));

        out.printf("public abstract class %s%s\n", extend.simpleName, extend.params);
        out.printf("        extends %s%s {\n", out.importName(extend.baseClassName), extend.baseParams);
        out.enter();
        {
            out.println();
            out.println("private static final long serialVersionUID = 1L;");

            TableOid oid = table.getId();
            out.println();
            if (oid.getCatalogName() != null)
                out.printf("public static final String CATALOG_NAME = %s;\n", //
                        StringQuote.qqJavaString(oid.getCatalogName()));
            if (oid.getSchemaName() != null)
                out.printf("public static final String SCHEMA_NAME = %s;\n", //
                        StringQuote.qqJavaString(oid.getSchemaName()));
            if (oid.getTableName() != null)
                out.printf("public static final String TABLE_NAME = %s;\n", //
                        StringQuote.qqJavaString(oid.getTableName()));

            templates.FIELD_consts(out, table, null, OutFormat.JAVA);
            templates.N_consts(out, table, null, OutFormat.JAVA);
            templates.ord_consts(out, table, null, OutFormat.JAVA);

            for (IColumnMetadata column : table.getColumns()) {
                if (column.isExcluded())
                    continue;

                if (column.isCompositeProperty()) {
                    checkCompositeProperty(table, column);
                    continue;
                }

                if (column.isForeignKey())
                    continue;

                out.println();
                templates.columnField(out, column);
            }

            for (CrossReference xref : table.getForeignKeys().values()) {
                if (xref.isExcluded(table))
                    continue;

                if (xref.isCompositeProperty())
                    continue;

                out.println();

                templates.foreignKeyField(out, xref, table);

                for (String fkColumnName : xref.getForeignKey().getColumnNames()) {
                    IColumnMetadata column = table.getColumn(fkColumnName);
                    out.println();
                    templates.columnField(out, column);
                }
            }

            IColumnMetadata[] primaryKeyCols = table.getPrimaryKeyColumns();
            boolean excluded = false;
            for (IColumnMetadata c : primaryKeyCols)
                excluded |= c.isExcluded();
            if (excluded) {
            } else if (primaryKeyCols.length > 1) {
                // K id() => new Foo_Id(this)
                out.println();
                out.println("@Override");
                out.printf("public %s id() {\n", out.importName(idType));
                // out.printf(" return id;\n");
                out.printf("    return new %s(this);\n", out.importName(project.Foo_Id.qName));
                out.printf("}\n");

                // id(Foo_Id id) => { this.field* = id.property* }
                out.println();
                out.println("@Override");
                out.printf("public void id(%s id) {\n", out.importName(idType));
                // out.printf(" this.id = id;\n");
                for (IColumnMetadata k : primaryKeyCols) {
                    ColumnNaming cname = project.naming(k);
                    out.printf("    this.%s = id.get%s();\n", cname.fieldName, cname.ucfirstPropertyName);
                }
                out.printf("}\n");
            } else {
                for (IColumnMetadata k : primaryKeyCols) {
                    ColumnNaming cname = project.naming(k);
                    out.println();
                    out.println("@Override");
                    out.printf("public %s id() {\n", out.importName(idType));
                    out.printf("    return get%s();\n", cname.ucfirstPropertyName);
                    out.printf("}\n");
                    out.println();
                    out.println("@Override");
                    out.printf("public void id(%s id) {\n", out.importName(idType));
                    out.printf("    set%s(id);\n", cname.ucfirstPropertyName);
                    out.printf("}\n");
                }
            }

            for (IColumnMetadata column : table.getColumns()) {
                if (column.isExcluded())
                    continue;

                if (column.isCompositeProperty())
                    continue;

                if (column.isForeignKey())
                    continue;

                out.println();
                templates.columnAccessors(out, column, true);
            }

            for (CrossReference xref : table.getForeignKeys().values()) {
                if (xref.isExcluded(table))
                    continue;

                if (xref.isCompositeProperty())
                    continue;

                out.println();
                templates.foreignKeyAccessors(out, xref, table);

                IColumnMetadata[] fv = xref.getForeignColumns();
                IColumnMetadata[] pv = xref.getParentColumns();
                for (int i = 0; i < fv.length; i++) {
                    IColumnMetadata f = fv[i];
                    if (f.isExcluded())
                        continue;

                    IColumnMetadata p = pv[i];
                    out.println();
                    templates.foreignKeyColumnAccessors(out, xref, f, p, true);
                }
            }

            out.println();
            templates.initNotNulls(out, table);

            out.leave();
        }
        out.println();
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

}
