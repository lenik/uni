package net.bodz.lily.tool.daogen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.potato.PotatoTypes;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.meta.CriteriaClass;
import net.bodz.lily.model.base.*;
import net.bodz.lily.security.CoPrincipal;
import net.bodz.lily.security.dao.CoPrincipalMask;
import net.bodz.lily.t.base.CoMessage;
import net.bodz.lily.t.base.CoMessageMask;
import net.bodz.lily.template.CoCategory;
import net.bodz.lily.template.CoCategoryMask;

public class FooMask_stuff__java
        extends JavaGen__java {

    public FooMask_stuff__java(JavaGenProject project) {
        super(project, project._FooMask_stuff);
    }

    static Map<Class<?>, Class<?>> defaultMaskBases = new LinkedHashMap<>();
    static {
        defaultMaskBases.put(CoMessage.class, CoMessageMask.class);
        defaultMaskBases.put(CoMomentInterval.class, CoMomentIntervalMask.class);
        defaultMaskBases.put(CoCategory.class, CoCategoryMask.class);
        defaultMaskBases.put(CoCode.class, CoCodeMask.class);
        defaultMaskBases.put(CoNode.class, CoNodeMask.class);
        defaultMaskBases.put(CoPrincipal.class, CoPrincipalMask.class);
    };

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        String javaType = table.getBaseTypeName();
        Class<?> parentClass = null;

        if (javaType != null) {
            try {
                Class<?> entityClass = Class.forName(javaType);

                CriteriaClass aCriteriaClass = entityClass.getAnnotation(CriteriaClass.class);
                if (aCriteriaClass != null) {
                    Class<?> criteriaClass = aCriteriaClass.value();
                    parentClass = criteriaClass;
                } else {
                    for (Class<?> base : defaultMaskBases.keySet()) {
                        if (base.isAssignableFrom(entityClass)) {
                            parentClass = defaultMaskBases.get(base);
                            break;
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        if (parentClass == null) {
            QualifiedName idType = templates.getIdType(table);
            if (idType != null)
                parentClass = CoObjectMask.class;
            else
                parentClass = StructRowMask.class;
        }

        IType parentType = PotatoTypes.getInstance().loadType(parentClass);
        String parentSimpleName = out.im.name(parentClass);

        List<IColumnMetadata> columns = new ArrayList<>();
        for (IColumnMetadata column : table.getColumns()) {
            ColumnName cname = project.columnName(column);
            if (cname.property.isEmpty() || cname.property.equals("-"))
                continue;
            if (column.isCompositeProperty())
                continue;
            if (parentType != null && parentType.getProperty(cname.property) != null)
                continue; // exclude inherited ones.
            columns.add(column);
        }

        out.printf("public class %s\n", project._FooMask_stuff.name);
        out.printf("        extends %s {\n", parentSimpleName);
        out.enter();
        {
//            out.println();
//            out.println("private static final long serialVersionUID = 1L;");

            for (IColumnMetadata column : columns) {
                out.println();
                templates.columnMaskFields(out, column);
            }

            for (IColumnMetadata column : columns) {
                out.println();
                templates.columnMaskAccessors(out, column);
            }

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
