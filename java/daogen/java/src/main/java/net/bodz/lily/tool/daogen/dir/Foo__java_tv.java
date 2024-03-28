package net.bodz.lily.tool.daogen.dir;

import javax.persistence.Table;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.lily.entity.PrimaryKeyColumns;
import net.bodz.lily.entity.PrimaryKeyProperties;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo__java_tv
        extends JavaGen__java {

    public Foo__java_tv(JavaGenProject project) {
        super(project, project.Foo);
    }

    static void annotateStrings(JavaSourceWriter out, Class<?> aClass, String... values) {
        out.print("@" + out.im.name(aClass) + "(");
        if (values.length == 1) {
            out.print(StringQuote.qqJavaString(values[0]));
        } else {
            out.println();
            out.enter();
            for (int i = 0; i < values.length; i++) {
                if (i != 0)
                    out.print(",");
                out.print(StringQuote.qqJavaString(values[i]));
                out.println();
            }
            out.leave();
        }
        out.println(")");
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        TableOid oid = table.getId();

        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project.Foo.qName, project._Foo_stuff.qName);

        String simpleName = extend.type.name;

        IColumnMetadata[] keyCols = table.getPrimaryKeyColumns();
        if (keyCols != null && keyCols.length != 0 && extend.javaBaseClass != null) {
            String[] colNames = new String[keyCols.length];
            for (int i = 0; i < keyCols.length; i++)
                colNames[i] = keyCols[i].getName();

            String[] colProps = new String[keyCols.length];
            for (int i = 0; i < keyCols.length; i++) {
                ColumnNaming cname = project.naming(keyCols[i]);
                colProps[i] = cname.propertyName;
            }

            PrimaryKeyColumns aColumns = extend.javaBaseClass.getAnnotation(PrimaryKeyColumns.class);
            if (aColumns == null || ! arrayEquals(aColumns.value(), colNames)) {
                annotateStrings(out, PrimaryKeyColumns.class, colNames);
            }

            PrimaryKeyProperties aProperties = extend.javaBaseClass.getAnnotation(PrimaryKeyProperties.class);
            if (aProperties == null || ! arrayEquals(aProperties.value(), colProps)) {
                annotateStrings(out, PrimaryKeyProperties.class, colProps);
            }
        }

        out.print("@" + out.im.name(Table.class) + "(");
        {
            String catalog_name = oid.getCatalogName();
            if (catalog_name != null)
                out.printf("catalog = %s.CATALOG_NAME, ", simpleName);
            String schema_name = oid.getSchemaName();
            if (schema_name != null)
                out.printf("schema = %s.SCHEMA_NAME, ", simpleName);
            out.printf("name = %s.TABLE_NAME", simpleName);
            out.println(")");
        }

        out.printf("public class %s%s\n", //
                simpleName, extend.angledTypeVars());
        out.printf("        extends %s%s {\n", //
                out.importName(extend.baseType), //
                extend.angledBaseTypeArgs());
        out.enter();
        {
            out.println();
            out.println("private static final long serialVersionUID = 1L;");

            out.leave();
        }
        out.println();
        out.println("}");
    }

    boolean arrayEquals(String[] a, String[] b) {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (! a[i].equals(b[i]))
                return false;
        return true;
    }

}
