package net.bodz.lily.tool.daogen.dir.dao.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.bodz.bas.c.java.lang.OptionNames;
import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.object.Unknown;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanProperty;
import net.bodz.bas.rtx.IMutableOptions;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.concrete.CoObject;
import net.bodz.lily.entity.type.EntityTypes;
import net.bodz.lily.entity.type.IEntityTypeInfo;
import net.bodz.lily.test.TestSampleBuilder;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;
import net.bodz.lily.tool.daogen.util.DefaultJavaConstructs;
import net.bodz.lily.tool.daogen.util.IJavaConstructContext;
import net.bodz.lily.tool.daogen.util.IJavaConstructs;
import net.bodz.lily.tool.daogen.util.JavaConstructContext;
import net.bodz.lily.util.IRandomPicker;

public class FooSamples__java
        extends DaoGen__java {

    static final Logger logger = LoggerFactory.getLogger(FooSamples__java.class);

    SampleFactory sampleFactory;
    IJavaConstructs javaConstructs;

    public FooSamples__java(DaoGenProject project) {
        super(project, project.FooSamples);
    }

    @Override
    protected boolean isTestScoped() {
        return false;
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        sampleFactory = new MySampleFactory(project.randomSeed, Nullables.hashCode(table.getId()));
        javaConstructs = new DefaultJavaConstructs();

        out.println("public class " + project.FooSamples.name);
        out.enter();
        {
            out.enter();
            {
                out.printf("extends %s {\n", //
                        out.im.name(TestSampleBuilder.class));
                out.println();
                out.leave();
            }

            List<IColumnMetadata> columns = new ArrayList<>(table.getColumns());

            // foreign key references as member properties
            for (String fkName : table.getForeignKeys().keySet()) {
                CrossReference xref = table.getForeignKeys().get(fkName);
                if (xref.isCompositeProperty())
                    continue;
                for (IColumnMetadata foreignColumn : xref.getForeignColumns())
                    columns.remove(foreignColumn);
            }

            fields(out, table, columns);

            mtdBuild(out, table, columns);

            mtdWireAny(out, table);

            out.leave();
        }
        out.println("}");
    }

    void fields(JavaSourceWriter out, ITableMetadata table, List<IColumnMetadata> columns) {
        IType entityType = table.getPotatoType();
        Set<String> compositeHeads = templates.getCompositeHeads(table);

        // foreign key references as member properties
        for (String fkName : table.getForeignKeys().keySet()) {
            CrossReference xref = table.getForeignKeys().get(fkName);
            if (xref.isCompositeProperty())
                continue;
            QualifiedName parentClass = xref.getParentTable().getJavaType();
            out.printf("public %s %s;\n", //
                    out.im.name(parentClass), xref.getPropertyName());
            for (IColumnMetadata foreignColumn : xref.getForeignColumns())
                columns.remove(foreignColumn);
        }
        if (!table.getForeignKeys().isEmpty())
            out.println();

        for (String head : compositeHeads) {
            Class<?> propertyClass;
            if (entityType != null) {
                IProperty headProperty = entityType.getProperty(head);
                propertyClass = headProperty.getPropertyClass();
            } else {
                propertyClass = Unknown.class; // need user fix.
            }
            out.printf("public %s %s;\n", //
                    out.im.name(propertyClass), head);
        }
        if (!compositeHeads.isEmpty())
            out.println();
    }

    void mtdBuild(JavaSourceWriter out, ITableMetadata table, List<IColumnMetadata> columns) {
        Set<String> compositeHeads = templates.getCompositeHeads(table);

        out.println("@Override");
        out.printf("public %s build()\n", //
                out.im.name(project.Foo.qName));
        out.println("        throws Exception {");
        out.enter();
        {
            out.printf("%s a = new %s();\n", //
                    out.im.name(project.Foo.qName), //
                    out.im.name(project.Foo.qName));

            for (String fkName : table.getForeignKeys().keySet()) {
                CrossReference xref = table.getForeignKeys().get(fkName);
                if (xref.isCompositeProperty())
                    continue;

                String setterName;
                IProperty refProperty = xref.getProperty();
                if (refProperty != null) {
                    BeanProperty bp = (BeanProperty) refProperty;
                    Method setter = bp.getPropertyDescriptor().getWriteMethod();
                    if (setter == null)
                        continue;
                    setterName = setter.getName();
                } else {
                    String property = xref.getPropertyName();
                    setterName = "set" + Strings.ucfirst(property);
                }

                out.printf("a.%s(%s);\n", //
                        setterName, //
                        xref.getPropertyName());
            }

            IType entityType = table.getPotatoType();
            for (IColumnMetadata column : columns) {
                if (column.isExcluded()) // mixin?
                    continue;

                if (column.isPropertyOfComposite())
                    continue;

                if (!canWrite(entityType, column))
                    continue;

                makeEntry(out, column);
            }

            if (entityType != null)
                for (String head : compositeHeads) {
                    IProperty headProperty = entityType.getProperty(head);
                    if (headProperty == null)
                        continue;
                    Class<?> propertyClass = headProperty.getPropertyClass();
                    String compositeSamplesType = propertyClass.getName() + "Samples";
                    out.printf("a.set%s(new %s().build());\n", //
                            Strings.ucfirst(head), //
                            out.im.name(compositeSamplesType));
                }

            out.println("return a;");
            out.leave();
        }
        out.println("}");
        out.println();
    }

    void mtdWireAny(JavaSourceWriter out, ITableMetadata table) {
        out.println("@Override");
        out.printf("public %s wireAny(%s picker) {\n", //
                project.FooSamples.name, //
                out.im.name(IRandomPicker.class));
        // out.println(" throws Exception {");
        out.enter();
        {
            for (String fkName : table.getForeignKeys().keySet()) {
                CrossReference xref = table.getForeignKeys().get(fkName);
                if (xref.isCompositeProperty())
                    continue;

                QualifiedName parentClassName = xref.getParentTable().getJavaType();
                String parentMapperName = null;

                IType parentType = xref.getParentTable().getPotatoType();
                if (parentType != null) {
                    IEntityTypeInfo parentInfo = EntityTypes.getTypeInfo(parentType.getJavaClass());
                    if (parentInfo != null) {
                        Class<?> mc = parentInfo.getMapperClass();
                        if (mc != null)
                            parentMapperName = mc.getCanonicalName();
                    }
                }
                if (parentMapperName == null) {
                    parentMapperName = parentClassName.packageName + ".dao." + parentClassName.name + "Mapper";
                }

                String templateField = xref.getPropertyName();

                // TODO need to update all entity classes before using TABLE_NAME.
                // String parentClass = xref.getParentTable().getJavaQName();

                out.printf("this.%s = picker.pickAny(%s.class, \"%s\");\n", //
                        templateField, //
                        out.im.name(parentMapperName), //
                        // out.im.name(parentClass)
                        xref.getParentTable().getName() //
                );
            }

            out.println("return this;");
            out.leave();
        }
        out.println("}");
        out.println();

        out.println("@Override");
        out.printf("public %s buildWired(%s picker)", //
                out.im.name(project.Foo.qName), //
                out.im.name(IRandomPicker.class));
        out.println(" throws Exception {");
        out.enter();
        {
            out.printf("return wireAny(picker).build();\n");
            out.leave();
        }
        out.println("}");
        out.println();
    }

    void makeEntry(JavaSourceWriter out, IColumnMetadata column) {
        assert !column.isForeignKey();

        Class<?> columnType = column.getJavaClass();
        assert !CoObject.class.isAssignableFrom(columnType);

        ColumnNaming cname = project.naming(column);

        IMutableOptions options = sampleFactory.newOptions(column);
        options.addOption(OptionNames.signed, column.isSigned());
        options.addOption(Random.class, sampleFactory.random);

        Object sample = sampleFactory.newSample(column, options);

        IJavaConstructContext context = new JavaConstructContext(out);
        String javaConstruct = javaConstructs.construct(sample, context);

        if (javaConstruct != null)
            out.printf("a.set%s(%s);\n", cname.capPropertyName, javaConstruct);
    }

    static boolean canWrite(IType type, IColumnMetadata column) {
        if (type != null) {
            IProperty property = column.getProperty();
            if (property == null) {
                String propertyName = column.getJavaName();
                if (propertyName != null)
                    property = type.getProperty(propertyName);
            }
            if (property == null) {
                // return false;
            } else {
                if (!property.isWritable()) // read-only column/property.
                    return false;
            }
        }
        return true;
    }

}
