package net.bodz.lily.tool.daogen.dir.dao;

import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.potato.PotatoTypes;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.concrete.CoObjectCriteriaBuilder;
import net.bodz.lily.concrete.StructRowCriteriaBuilder;
import net.bodz.lily.entity.manager.ForEntityType;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;

public class FooCriteriaBuilder_stuff__java
        extends DaoGen__java {

    public FooCriteriaBuilder_stuff__java(DaoGenProject project) {
        super(project, project._FooCriteriaBuilder_stuff);
    }

    Class<?> findMaskClass(Class<?> clazz) {
        Class<?> ancestor = clazz;
        while (ancestor != null) {
            QualifiedName javaType = QualifiedName.of(ancestor);
            QualifiedName maskType = javaType.append("CriteriaBuilder");
            Class<?> maskClass = maskType.getJavaClass();
            if (maskClass != null)
                return maskClass;
            ancestor = ancestor.getSuperclass();
        }
        return null;
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        QualifiedName javaType = table.getJavaType();
        Class<?> parentClass = null;

        if (javaType != null) {
            Class<?> entityClass = javaType.getJavaClass();
            if (entityClass != null)
                parentClass = findMaskClass(entityClass);
        }

        if (parentClass == null) {
            QualifiedName idType = templates.getIdType(table);
            if (idType != null)
                parentClass = CoObjectCriteriaBuilder.class;
            else
                parentClass = StructRowCriteriaBuilder.class;
        }

        IType parentType = PotatoTypes.getInstance().loadType(parentClass);
        String parentSimpleName = out.im.name(parentClass);

        List<IColumnMetadata> columns = new ArrayList<>();
        for (IColumnMetadata column : table.getColumns()) {
            ColumnNaming cname = project.naming(column);
            if (cname.propertyName.isEmpty() || cname.propertyName.equals("-"))
                continue;
            if (column.isCompositeProperty())
                continue;
            if (parentType != null && parentType.getProperty(cname.propertyName) != null)
                continue; // exclude inherited ones.
            columns.add(column);
        }

        String className = project._FooCriteriaBuilder_stuff.name;

        out.printf("@%s(%s.class)\n", //
                out.importName(ForEntityType.class), //
                out.importName(javaType));
        out.printf("public class %s<self_t extends %s<self_t>>\n", className, className);
        out.printf("        extends %s<self_t> {\n", parentSimpleName);
        out.enter();
        {
            out.println();

            boolean lastAny = false;
            for (IColumnMetadata column : columns) {
                if (lastAny)
                    out.println();
                lastAny = templates.columnCriteriaBuilderFields(out, column);
            }

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
