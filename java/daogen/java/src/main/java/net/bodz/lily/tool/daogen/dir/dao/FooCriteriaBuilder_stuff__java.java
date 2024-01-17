package net.bodz.lily.tool.daogen.dir.dao;

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
import net.bodz.lily.security.dao.CoPrincipalCriteriaBuilder;
import net.bodz.lily.t.base.CoMessage;
import net.bodz.lily.t.base.CoMessageCriteriaBuilder;
import net.bodz.lily.template.CoCategory;
import net.bodz.lily.template.CoCategoryCriteriaBuilder;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.tool.daogen.util.CanonicalClass;

public class FooCriteriaBuilder_stuff__java
        extends JavaGen__java {

    public FooCriteriaBuilder_stuff__java(JavaGenProject project) {
        super(project, project._FooCriteriaBuilder_stuff);
    }

    static Map<Class<?>, Class<?>> defaultBases = new LinkedHashMap<>();
    static {
        defaultBases.put(CoMessage.class, CoMessageCriteriaBuilder.class);
        defaultBases.put(CoMomentInterval.class, CoMomentIntervalCriteriaBuilder.class);
        defaultBases.put(CoCategory.class, CoCategoryCriteriaBuilder.class);
        defaultBases.put(CoCode.class, CoCodeCriteriaBuilder.class);
        defaultBases.put(CoNode.class, CoNodeCriteriaBuilder.class);
        defaultBases.put(CoPrincipal.class, CoPrincipalCriteriaBuilder.class);
    };

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        String javaType = table.getJavaQName();
        Class<?> parentClass = null;

        if (javaType != null) {
            try {
                Class<?> entityClass = CanonicalClass.forName(javaType);

                for (Class<?> base : defaultBases.keySet()) {
                    if (base.isAssignableFrom(entityClass)) {
                        parentClass = defaultBases.get(base);
                        break;
                    }
                }
                CriteriaClass aCriteriaClass = entityClass.getAnnotation(CriteriaClass.class);
                if (aCriteriaClass != null) {
                    Class<?> criteriaClass = aCriteriaClass.value();
                    if (parentClass == null)
                        parentClass = criteriaClass;
                    else if (parentClass.isAssignableFrom(criteriaClass))
                        parentClass = criteriaClass;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
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
