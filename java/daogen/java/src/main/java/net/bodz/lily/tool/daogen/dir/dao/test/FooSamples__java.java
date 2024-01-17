package net.bodz.lily.tool.daogen.dir.dao.test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import net.bodz.bas.c.java.lang.OptionNames;
import net.bodz.bas.c.java.util.Dates;
import net.bodz.bas.c.primitive.Primitives;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.codegen.EnglishTextGenerator;
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
import net.bodz.bas.typer.Typers;
import net.bodz.bas.typer.std.ISampleGenerator;
import net.bodz.lily.entity.type.EntityTypes;
import net.bodz.lily.entity.type.IEntityTypeInfo;
import net.bodz.lily.model.base.CoObject;
import net.bodz.lily.test.TestSampleBuilder;
import net.bodz.lily.tool.daogen.ColumnName;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__java;
import net.bodz.lily.util.IRandomPicker;

public class FooSamples__java
        extends JavaGen__java {

    static final Logger logger = LoggerFactory.getLogger(FooSamples__java.class);

    int maxStringLen = 1000;

    public FooSamples__java(JavaGenProject project) {
        super(project, project.FooSamples);
    }

    Random random(Object obj) {
        int prime = 17;
        long seed = project.randomSeed;
        if (obj != null)
            seed += prime * obj.hashCode();
        return new Random(seed);
    }

    EnglishTextGenerator en(Object obj) {
        Random random = random(obj);
        return new EnglishTextGenerator(random);
    }

    Random random;
    EnglishTextGenerator enGen;

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        random = random(table.getId());
        enGen = en(table.getId());

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
        IType entityType = table.getEntityType();
        Set<String> compositeHeads = templates.getCompositeHeads(table);

        // foreign key references as member properties
        for (String fkName : table.getForeignKeys().keySet()) {
            CrossReference xref = table.getForeignKeys().get(fkName);
            if (xref.isCompositeProperty())
                continue;
            String parentClassName = xref.getParentTable().getEntityTypeName();
            out.printf("public %s %s;\n", //
                    out.im.name(parentClassName), xref.getJavaName());
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
                propertyClass = Object.class; // need user fix.
            }
            out.printf("public %s %s;\n", //
                    out.im.name(propertyClass), head);
        }
        if (!compositeHeads.isEmpty())
            out.println();
    }

    void mtdBuild(JavaSourceWriter out, ITableMetadata table, List<IColumnMetadata> columns) {
        IType entityType = table.getEntityType();
        Set<String> compositeHeads = templates.getCompositeHeads(table);

        out.println("@Override");
        out.printf("public %s build()\n", //
                out.im.name(project.Foo));
        out.println("        throws Exception {");
        out.enter();
        {
            out.printf("%s a = new %s();\n", //
                    out.im.name(project.Foo), out.im.name(project.Foo));

            for (String fkName : table.getForeignKeys().keySet()) {
                CrossReference xref = table.getForeignKeys().get(fkName);
                if (xref.isCompositeProperty())
                    continue;

                String setterName;
                if (entityType != null) {
                    IProperty refProperty = entityType.getProperty(xref.getJavaName());
                    if (refProperty == null) {
                        logger.warn("no property for xref " + fkName);
                        continue;
                    }

                    BeanProperty bp = (BeanProperty) refProperty;
                    Method setter = bp.getPropertyDescriptor().getWriteMethod();
                    setterName = setter.getName();
                } else {
                    String property = xref.getJavaName();
                    setterName = "set" + Strings.ucfirst(property);
                }

                out.printf("a.%s(%s);\n", //
                        setterName, //
                        xref.getJavaName());
            }

            for (IColumnMetadata column : columns) {
                if (column.isExcluded()) // mixin?
                    continue;

                if (column.isCompositeProperty())
                    continue;

                if (!canWrite(entityType, column))
                    continue;

                makeEntry(out, column);
            }

            if (entityType != null)
                for (String head : compositeHeads) {
                    IProperty headProperty = entityType.getProperty(head);
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
        IType entityType = table.getEntityType();
        Set<String> compositeHeads = templates.getCompositeHeads(table);

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

                // TODO need to update all entity classes before using TABLE_NAME.
                // String parentClass = xref.getParentTable().getJavaQName();

                out.printf("this.%s = picker.pickAny(%s.class, \"%s\");\n", //
                        templateField, //
                        out.im.name(parentMapperName), //
                        // out.im.name(parentClass)
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
                    out.printf("this.%s = new %s().build();\n", //
                            templateField, //
                            out.im.name(samplesClassName));
                }

            out.println("return this;");
            out.leave();
        }
        out.println("}");
        out.println();

        out.println("@Override");
        out.printf("public %s buildWired(%s picker)", //
                out.im.name(project.Foo.getFullName()), //
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
        ColumnName cname = project.columnName(column);
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
        }

        if (preferredType == String.class) {
            int maxLen = column.getPrecision();
            if (maxLen <= 0)
                throw new IllegalArgumentException("invalid varchar length: " + maxLen);
            if (maxLen > maxStringLen)
                maxLen = maxStringLen;
            int wordMaxLen = 10;
            String sample = enGen.makeText(maxLen, wordMaxLen);
            javaExpr = StringQuote.qqJavaString(sample.toString());

        } else if (preferredType == BigDecimal.class) {
            int precision = column.getPrecision();
            int scale = column.getScale();
            int intLen = precision;
            if (scale != 0)
                intLen -= scale; // + 1 (dot);

            StringBuilder sb = new StringBuilder(precision);
            randomDigits(sb, random.nextInt(intLen + 1), true, random);

            if (scale != 0 && random.nextBoolean()) {
                sb.append('.');
                randomDigits(sb, scale, false, random);
            }

            out.im.name(BigDecimal.class);
            javaExpr = "new BigDecimal(\"" + sb + "\")";

        } else if (preferredType == BigInteger.class) {
            int maxLen = column.getPrecision();
            int len = random.nextInt(maxLen) + 1;
            StringBuilder sb = new StringBuilder(len);
            randomDigits(sb, random.nextInt(len + 1), true, random);
            out.im.name(BigInteger.class);
            javaExpr = "new BigInteger(\"" + sb + "\")";

        } else {
            ISampleGenerator<?> generator = Typers.getTyper(preferredType, ISampleGenerator.class);
            // IToJavaCode toJavaCode= Typers.getTyper(type, IToJavaCode.class);
            if (generator != null) {
                Options options = new Options();
                options.addOption(OptionNames.signed, false);
                options.addOption(Random.class, random);

                Object sample = generator.newSample(options);

                if (sample instanceof Date) {
                    String iso = Dates.ISO8601Z.format(sample);
                    String isoQuoted = StringQuote.qqJavaString(iso);

                    String timeLong = String.format("%s.%s.parse(%s).getTime()", //
                            out.im.name(Dates.class), //
                            "ISO8601Z", // Dates.ISO8601Z
                            isoQuoted);

                    if (actualType != null) {
                        if (typesAcceptInstant.contains(actualType)) {
                            javaExpr = "new " + out.im.name(actualType) //
                                    + "(" + timeLong + ")";
                        }
                    }
                } else {
                    javaExpr = sample.toString();
                    switch (TypeKind.getTypeId(preferredType)) {
                    case TypeId._byte:
                    case TypeId.BYTE:
                        javaExpr = "(byte)" + javaExpr;
                        break;

                    case TypeId._short:
                    case TypeId.SHORT:
                        javaExpr = "(short)" + javaExpr;
                        break;

                    case TypeId._long:
                    case TypeId.LONG:
                        javaExpr = javaExpr + "L";
                        break;
                    }
                }
            }
        }

        if (javaExpr != null)
            out.printf("a.set%s(%s);\n", cname.Property, javaExpr);
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

    static Set<Class<?>> typesAcceptInstant = new HashSet<>();
    static {
        typesAcceptInstant.add(DateTime.class);
        typesAcceptInstant.add(LocalDateTime.class);
        typesAcceptInstant.add(Date.class);
        typesAcceptInstant.add(java.sql.Date.class);
        typesAcceptInstant.add(Timestamp.class);
    }

    void randomDigits(StringBuilder sb, int len, boolean noZeroStart, Random random) {
        for (int i = 0; i < len; i++) {
            int digit;
            while (true) {
                digit = random.nextInt(10);
                if (i == 0)
                    if (noZeroStart && digit == 0)
                        continue;
                break;
            }
            char ch = (char) ('0' + digit);
            sb.append(ch);
        }
    }

}
