package net.bodz.lily.tool.daogen;

import java.util.Set;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.entity.type.EntityTypes;
import net.bodz.lily.entity.type.IEntityTypeInfo;
import net.bodz.lily.test.AbstractTableTest;

public class FooMapperTest__java
        extends JavaGen__java {

    public FooMapperTest__java(JavaGenProject project) {
        super(project, project.FooMapperTest);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata model) {
        out.println("public class " + project.FooMapperTest.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s<%s, %s, %s> {\n", //
                        out.im.name(AbstractTableTest.class), //
                        out.im.name(project.Foo), //
                        out.im.name(project.FooMask), //
                        project.FooMapper.name);
                out.leave();
            }

            buildMapperMethods(out, model);

            out.println();
            out.leave();
        }
        out.println("}");
    }

    protected void buildMapperMethods(JavaSourceWriter out, ITableMetadata table) {
        IType entityType = table.getEntityType();
        Set<String> compositeHeads = templates.getCompositeHeads(table);

        out.println();
        out.println("@Override");
        out.printf("public %s buildSample()\n", //
                out.im.name(project.Foo));
        out.println("        throws Exception {");
        out.enter();
        {
            out.printf("%s a = new %s();\n", //
                    out.im.name(project.FooSamples), //
                    out.im.name(project.FooSamples));

            for (String fkName : table.getForeignKeys().keySet()) {
                CrossReference xref = table.getForeignKeys().get(fkName);
                if (xref.isCompositeProperty())
                    continue;

                String parentClassName = xref.getParentTable().getEntityTypeName();
                String parentMapperName = null;

                IType parentType = xref.getParentTable().getEntityType();
                if (parentType != null) {
                    IEntityTypeInfo parentInfo = EntityTypes.getTypeInfo(parentType.getJavaClass());
                    if (parentInfo != null) {
                        Class<?> mc = parentInfo.getMapperClass();
                        if (mc != null)
                            parentMapperName = mc.getCanonicalName();
                    }
                }
                if (parentMapperName == null) {
                    int lastDot = parentClassName.lastIndexOf('.');
                    String pkg = parentClassName.substring(0, lastDot);
                    String simple = parentClassName.substring(lastDot + 1);
                    parentMapperName = pkg + ".dao." + simple + "Mapper";
                }

                String templateField = xref.getJavaName();

                out.printf("a.%s = tables.pickAny(%s.class, \"%s\");\n", //
                        templateField, //
                        out.im.name(parentMapperName), //
                        xref.getParentTable().getName() //
                );
            }

            if (entityType != null)
                for (String head : compositeHeads) {
                    IProperty headProperty = entityType.getProperty(head);
                    if (headProperty == null)
                        continue;
                    Class<?> type = headProperty.getPropertyClass();
                    String samplesClassName = type.getName() + "Samples";
                    String templateField = head;
                    out.printf("a.%s = new %s().build();\n", //
                            templateField, //
                            out.im.name(samplesClassName));
                }

            out.println("return a.build();");
            out.leave();
        }
        out.println("}");
    }

}
