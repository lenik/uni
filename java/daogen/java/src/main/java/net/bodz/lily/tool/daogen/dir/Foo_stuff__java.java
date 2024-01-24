package net.bodz.lily.tool.daogen.dir;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.concrete.CoEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.entity.IdType;
import net.bodz.lily.meta.TypeParamType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.tool.daogen.util.CanonicalClass;

public class Foo_stuff__java
        extends JavaGen__java {

    static final Logger logger = LoggerFactory.getLogger(Foo_stuff__java.class);

    public Foo_stuff__java(JavaGenProject project) {
        super(project, project._Foo_stuff);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        QualifiedName idType = templates.getIdType(table);

        String simpleName = project._Foo_stuff.name;
        String baseClassName = table.getBaseTypeName();

        String params = "";
        String baseParams = "";
        String typeAgain = null;

        if (baseClassName != null) {
            Class<?> baseClass;
            try {
                baseClass = CanonicalClass.forName(baseClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            baseClassName = out.im.name(baseClass); // import&compress

            TypeParameters aTypeParams = baseClass.getAnnotation(TypeParameters.class);
            if (aTypeParams != null) {
                StringBuilder paramsBuf = new StringBuilder();
                StringBuilder baseParamsBuf = new StringBuilder();
                StringBuilder recBuf = new StringBuilder();
                StringBuilder typeAgainParams = new StringBuilder();
                for (TypeParamType param : aTypeParams.value()) {
                    switch (param) {
                    case ID_TYPE:
                        baseParamsBuf.append(", " + idType.name);
                        break;
                    case THIS_TYPE:
                        baseParamsBuf.append(", " + simpleName);
                        break;
                    case THIS_REC:
                        paramsBuf.append(", this_t extends " + simpleName + "<%R>");
                        baseParamsBuf.append(", this_t");
                        recBuf.append(", this_t");
                        typeAgainParams.append(", " + out.im.simpleId(TypeParamType.THIS_TYPE));
                        break;
                    default:
                        baseParamsBuf.append(", ?");
                    }
                }
                String rec = recBuf.length() == 0 ? "" : recBuf.substring(2);
                params = paramsBuf.length() == 0 ? "" : ("<" + paramsBuf.substring(2) + ">");
                params = params.replace("%R", rec);
                baseParams = baseParamsBuf.length() == 0 ? "" : "<" + baseParamsBuf.substring(2) + ">";
                typeAgain = typeAgainParams.length() == 0 ? null : typeAgainParams.substring(2);
            }
        } else if (idType != null) {
            baseClassName = out.im.name(CoEntity.class);
            baseParams = "<" + out.im.name(idType) + ">";
        } else
            baseClassName = out.im.name(StructRow.class);

        String description = table.getDescription();
        if (description != null)
            templates.javaDoc(out, description);

        if (typeAgain != null)
            out.printf("@%s({ %s })\n", //
                    out.im.name(TypeParameters.class), //
                    typeAgain);

        if (idType != null)
            out.printf("@%s(%s.class)\n", //
                    out.im.name(IdType.class), //
                    out.im.name(idType));

        out.printf("public abstract class %s%s\n", simpleName, params);
        out.printf("        extends %s%s {\n", baseClassName, baseParams);
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

            templates.FIELD_consts(out, table, null);
            templates.N_consts(out, table, null);
            templates.ord_consts(out, table, null);

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
                out.printf("public %s id() {\n", out.im.name(idType));
                // out.printf(" return id;\n");
                out.printf("    return new %s(this);\n", out.im.name(project.Foo_Id));
                out.printf("}\n");

                // id(Foo_Id id) => { this.field* = id.property* }
                out.println();
                out.println("@Override");
                out.printf("public void id(%s id) {\n", out.im.name(idType));
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
                    out.printf("public %s id() {\n", out.im.name(idType));
                    out.printf("    return get%s();\n", cname.ucfirstPropertyName);
                    out.printf("}\n");
                    out.println();
                    out.println("@Override");
                    out.printf("public void id(%s id) {\n", out.im.name(idType));
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
