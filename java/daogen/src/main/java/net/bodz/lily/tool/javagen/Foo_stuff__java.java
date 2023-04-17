package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.entity.IdType;
import net.bodz.lily.meta.TypeParamType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.model.base.CoEntity;
import net.bodz.lily.model.base.StructRow;

public class Foo_stuff__java
        extends JavaGen__java {

    public Foo_stuff__java(JavaGenProject project) {
        super(project, project._Foo_stuff);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        QualifiedName idType = templates.getIdType(table);

        String javaType = table.getJavaType();
        String parent;
        if (javaType != null) {
            Class<?> javaClass;
            try {
                javaClass = Class.forName(javaType);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            parent = out.im.name(javaClass);

            TypeParameters aTypeParams = javaClass.getAnnotation(TypeParameters.class);
            if (aTypeParams != null) {
                StringBuilder sb = new StringBuilder();
                for (TypeParamType param : aTypeParams.value()) {
                    if (sb.length() != 0)
                        sb.append(", ");
                    switch (param) {
                    case ID_TYPE:
                        sb.append(idType.name);
                        break;
                    case THIS_TYPE:
                        sb.append(project._Foo_stuff.name);
                        break;
                    default:
                        sb.append("?");
                    }
                }
                parent += "<" + sb + ">";
            }
        } else if (idType != null)
            parent = out.im.name(CoEntity.class) + "<" + out.im.name(idType) + ">";
        else
            parent = out.im.name(StructRow.class);

        String description = table.getDescription();
        if (description != null)
            templates.javaDoc(out, description);

        if (idType != null)
            out.printf("@%s(%s.class)\n", //
                    out.im.name(IdType.class), //
                    out.im.name(idType));

        out.printf("public abstract class %s\n", project._Foo_stuff.name);
        out.printf("        extends %s {\n", parent);
        out.enter();
        {
            out.println();
            out.println("private static final long serialVersionUID = 1L;");

            templates.N_consts(out, table, null);
            templates.O_consts(out, table, null);

            for (IColumnMetadata column : table.getColumns()) {
                if (column.isExcluded())
                    continue;

                if (column.isCompositeProperty())
                    continue;

                if (column.isForeignKey())
                    continue;

                out.println();
                templates.columnField(out, column);
            }

            for (CrossReference xref : table.getForeignKeys().values()) {
                if (xref.isExcluded(table))
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
                    ColumnName cname = project.columnName(k);
                    out.printf("    this.%s = id.get%s();\n", cname.field, cname.Property);
                }
                out.printf("}\n");
            } else {
                for (IColumnMetadata k : primaryKeyCols) {
                    ColumnName cname = project.columnName(k);
                    out.println();
                    out.println("@Override");
                    out.printf("public %s id() {\n", out.im.name(idType));
                    out.printf("    return get%s();\n", cname.Property);
                    out.printf("}\n");
                    out.println();
                    out.println("@Override");
                    out.printf("public void id(%s id) {\n", out.im.name(idType));
                    out.printf("    this.set%s(id);\n", cname.Property);
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

}
