package net.bodz.lily.tool.daogen.dir;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Objects;

import net.bodz.bas.codegen.JavaImports;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.meta.decl.NotNull;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.entity.AbstractIdentity;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;
import net.bodz.lily.tool.daogen.OutFormat;

public class Foo_Id__java
        extends DaoGen__java {

    public Foo_Id__java(DaoGenProject project) {
        super(project, project.Foo_Id);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata tableView) {
        ITableMetadata table = tableView;

        IColumnMetadata[] primaryKeyColumns = table.getPrimaryKeyColumns();
        switch (primaryKeyColumns.length) {
            case 0:
                throw new IllegalArgumentException("no primary key column.");
        }

        for (IColumnMetadata primaryKeyCol : primaryKeyColumns)
            if (primaryKeyCol.isExcluded())
                throw new IllegalUsageException("Can't exclude primary key column");

        out.printf("public class %s\n", project.Foo_Id.name);
        out.printf("        extends %s\n", //
                out.im.name(AbstractIdentity.class));
        out.printf("        implements %s, %s {\n", //
                project.IFoo_Id.name, //
                out.im.name(Serializable.class));
        out.enter();
        {
            out.println();
            out.println("@" + out.importName(Serial.class));
            out.println("private static final long serialVersionUID = 1L;");

            templates.FIELD_consts(out, table, true, OutFormat.JAVA_CLASS);
            templates.N_consts(out, table, true, OutFormat.JAVA_CLASS);
            templates.ord_consts(out, table, true, OutFormat.JAVA_CLASS);

            for (IColumnMetadata column : primaryKeyColumns) {
                out.println();
                templates.columnField(out, column, Modifier.FINAL, false);
            }

            out.println();
            ctors(out, table, out.im);

            // getType
            out.println();
            getTypeMethod(out);

            // getters
            for (IColumnMetadata column : primaryKeyColumns) {
                out.println();
                templates.columnAccessors(out, column, true, true, false);
            }

            // alters
            for (IColumnMetadata column : primaryKeyColumns) {
                out.println();
                templates.fieldAlter(out, project.Foo_Id.name, column, true, primaryKeyColumns);
            }

            out.println();
            hashCode(out, table, out.im);

            out.println();
            equals(out, table, out.im);

            out.println();
            toString(out, table, out.im);

            out.leave();
        }
        out.println();
        out.println("}");
    }

    /**
     * <pre>
     *     @Override
     *     public FooIdTypeInfo getType() {
     *         return FooIdTypeInfo.INSTANCE;
     *     }
     * </pre>
     */
    void getTypeMethod(JavaSourceWriter out) {
        out.printf("public %s getType() {\n", project.Foo_IdTypeInfo.name);
        out.enter();
        out.printf("return %s.INSTANCE;\n", project.Foo_IdTypeInfo.name);
        out.leave();
        out.println("}");
    }

    void defaultCtor(JavaSourceWriter out) {
        out.println("public " + project.Foo_Id.name + "() {");
        out.println("}");
    }

    void explicitCtor(JavaSourceWriter out, JavaImports imports, IColumnMetadata... columns) {
        out.print("public " + project.Foo_Id.name + "(");
        for (int i = 0; i < columns.length; i++) {
            IColumnMetadata column = columns[i];
            if (i != 0)
                out.print(", ");

            boolean notNull = !column.isNullable(true);
            Class<?> javaClass = column.getJavaClass();
            boolean primitive = javaClass != null && javaClass.isPrimitive();

            String annotations = "";
            if (notNull && !primitive)
                annotations = "@" + out.im.name(NotNull.class) + " ";

            ColumnNaming cname = project.naming(column);
            out.printf("%s%s %s", annotations, //
                    imports.name(javaClass), cname.fieldName);
        }
        out.println(") {");
        out.enter();
        {
            for (IColumnMetadata k : columns) {
                ColumnNaming cname = project.naming(k);
                out.printf("this.%s = %s;\n", cname.fieldName, cname.fieldName);
            }
            out.leave();
        }
        out.println("}");
    }

    void copyCtor(JavaSourceWriter out, IColumnMetadata... columns) {
        // clone constructor
        out.printf("public %s(%s o) {\n", project.Foo_Id.name, project.Foo_Id.name);
        out.enter();
        {
            for (IColumnMetadata k : columns) {
                ColumnNaming cname = project.naming(k);
                out.printf("this.%s = o.%s;\n", cname.fieldName, cname.fieldName);
            }
            out.leave();
        }
        out.println("}");
    }

    void fromEntityCtor(JavaSourceWriter out, String fromType, IColumnMetadata... columns) {
        out.printf("public %s(%s o) {\n", //
                project.Foo_Id.name, //
                fromType);
        out.enter();
        {
            for (IColumnMetadata k : columns) {
                ColumnNaming cname = project.naming(k);
                out.printf("this.%s = o.%s;\n", cname.fieldName, cname.fieldName);
            }
            out.leave();
        }
        out.println("}");

    }

    void ctors(JavaSourceWriter out, ITableMetadata table, JavaImports imports) {
//        defaultCtor(out);
//        out.println();

        IColumnMetadata[] primaryKeyColumns = table.getPrimaryKeyColumns();
        explicitCtor(out, imports, primaryKeyColumns);
        out.println();

        copyCtor(out, primaryKeyColumns);
        out.println();

        // from entity constructor
        fromEntityCtor(out, imports.name(project._Foo_stuff.qName), primaryKeyColumns);
        out.println();

        // clone()
        cloneMethod(out);
    }

    void cloneMethod(ITreeOut out) {
        out.println("@Override");
        out.println("public " + project.Foo_Id.name + " clone() {");
        out.enter();
        {
            out.println("return new " + project.Foo_Id.name + "(this);");
            out.leave();
        }
        out.println("}");
    }

    void hashCode(ITreeOut out, ITableMetadata table, JavaImports imports) {
        out.println("@" + imports.name(Override.class));
        out.println("public int hashCode() {");
        out.enter();

        int i = 0;
        out.print("return " + imports.name(Objects.class) + ".hash(");
        for (IColumnMetadata column : table.getPrimaryKeyColumns()) {
            if (i != 0)
                out.print(", ");
            ColumnNaming cname = project.naming(column);
            out.print(cname.fieldName);
            i++;
        }
        out.println(");");

        out.leave();
        out.println("}");
    }

    void equals(ITreeOut out, ITableMetadata table, JavaImports imports) {
        out.println("@" + imports.name(Override.class));
        out.println("public boolean equals(Object obj) {");
        out.enter();

        out.println("if (this == obj)");
        out.println("    return true;");
        out.println("if (obj == null)");
        out.println("    return false;");
        out.println("if (getClass() != obj.getClass())");
        out.println("    return false;");
        out.println(project.Foo_Id.name + " o = (" + project.Foo_Id.name + ") obj;");

        for (IColumnMetadata column : table.getPrimaryKeyColumns()) {
            ColumnNaming cname = project.naming(column);
            out.printf("if (! Objects.equals(%s, o.%s)) return false;\n", //
                    cname.fieldName, cname.fieldName);
        }
        out.println("return true;");
        out.leave();
        out.println("}");
    }

    void toString(ITreeOut out, ITableMetadata table, JavaImports imports) {
        out.println("@" + imports.name(Override.class));
        out.println("public String toString() {");
        out.enter();

        out.println("StringBuilder sb = new StringBuilder(100);");
        int i = 0;
        for (IColumnMetadata column : table.getPrimaryKeyColumns()) {
            ColumnNaming cname = project.naming(column);
            out.print("sb.append(\"");
            if (i != 0)
                out.print(", ");
            out.println(cname.fieldName + " \" + " + cname.fieldName + ");");
            i++;
        }
        out.println("return sb.toString();");

        out.leave();
        out.println("}");
    }

}
