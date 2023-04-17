package net.bodz.lily.tool.javagen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.meta.CriteriaClass;
import net.bodz.lily.model.base.CoObjectMask;
import net.bodz.lily.model.base.StructRowMask;

public class FooMask_stuff__java
        extends JavaGen__java {

    public FooMask_stuff__java(JavaGenProject project) {
        super(project, project._FooMask_stuff);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        String javaType = table.getJavaType();
        String parent = null;

        if (javaType != null) {
            try {
                Class<?> entityClass = Class.forName(javaType);
                CriteriaClass aCriteriaClass = entityClass.getAnnotation(CriteriaClass.class);
                if (aCriteriaClass != null) {
                    Class<?> criteriaClass = aCriteriaClass.value();
                    parent = out.im.name(criteriaClass);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        if (parent == null) {
            QualifiedName idType = templates.getIdType(table);
            if (idType != null)
                parent = out.im.name(CoObjectMask.class);
            else
                parent = out.im.name(StructRowMask.class);
        }

        out.printf("public class %s\n", project._FooMask_stuff.name);
        out.printf("        extends %s {\n", parent);
        out.enter();
        {
//            out.println();
//            out.println("private static final long serialVersionUID = 1L;");

            for (IColumnMetadata column : table.getColumns()) {
                if (column.isExcluded())
                    continue;
                out.println();
                templates.columnMaskFields(out, column);
            }

            for (IColumnMetadata column : table.getColumns()) {
                if (column.isExcluded())
                    continue;
                out.println();
                templates.columnMaskAccessors(out, column);
            }

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
