package net.bodz.lily.tool.daogen.dir.dao.test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.bodz.bas.c.java.lang.OptionNames;
import net.bodz.bas.c.object.Unknown;
import net.bodz.bas.c.primitive.Primitives;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.provider.bean.BeanProperty;
import net.bodz.bas.rtx.Options;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.predef.Predef;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.bas.typer.Typers;
import net.bodz.bas.typer.std.ISampleGenerator;
import net.bodz.lily.concrete.CoObject;
import net.bodz.lily.entity.type.EntityTypes;
import net.bodz.lily.entity.type.IEntityTypeInfo;
import net.bodz.lily.test.TestSampleBuilder;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__java;
import net.bodz.lily.util.IRandomPicker;

public class FooSamples__java
        extends DaoGen__java {

    static final Logger logger = LoggerFactory.getLogger(FooSamples__java.class);

    JavaSamples samples;

    public FooSamples__java(DaoGenProject project) {
        super(project, project.FooSamples);
    }

    @Override
    protected boolean isTest() {
        return false;
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        samples = new JavaSamples(project.randomSeed, table.getId(), out);

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
        if (! table.getForeignKeys().isEmpty())
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
        if (! compositeHeads.isEmpty())
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

                if (column.isCompositeProperty())
                    continue;

                if (! canWrite(entityType, column))
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
        ColumnNaming cname = project.naming(column);
        Class<?> preferredType = column.getJavaClass();

        if (CoObject.class.isAssignableFrom(preferredType)) {
            logger.warn("unexpected column of entity: " + cname);
            return;
        }

        String javaExpr = null;

        Class<?> actualType = null;
        IProperty property = column.getProperty();
        if (property != null) {
            actualType = property.getPropertyClass();
            actualType = Primitives.box(actualType);
            preferredType = actualType;
        }

        if (preferredType == String.class)
            javaExpr = samples.string(column.getPrecision());

        else if (preferredType == BigDecimal.class) {
            javaExpr = samples.bigDecimal(column.getPrecision(), column.getScale());

        } else if (preferredType == BigInteger.class) {
            javaExpr = samples.bigInteger(column.getPrecision());

        } else {
            ISampleGenerator<?> generator = Typers.getTyper(preferredType, ISampleGenerator.class);
            // IToJavaCode toJavaCode= Typers.getTyper(type, IToJavaCode.class);
            if (generator != null) {
                Options options = new Options();
                options.addOption(OptionNames.signed, false);
                options.addOption(Random.class, samples.random);

                Object sample = generator.newSample(options);

                if (sample instanceof Date) {
                    javaExpr = samples.date((Date) sample, preferredType);

                } else if (sample instanceof TemporalAccessor) {
                    javaExpr = samples.javaTime((TemporalAccessor) sample, preferredType);

                } else if (sample instanceof Predef<?, ?>) {
                    javaExpr = samples.predef((Predef<?, ?>) sample, preferredType);

                } else {
                    javaExpr = samples.simpleValue(sample, preferredType);
                    if (javaExpr == null)
                        javaExpr = samples.parseString(sample.toString(), preferredType);
                }
            }
        }

        if (javaExpr != null)
            out.printf("a.set%s(%s);\n", cname.capPropertyName, javaExpr);
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
                if (! property.isWritable()) // read-only column/property.
                    return false;
            }
        }
        return true;
    }

}
