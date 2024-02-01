package net.bodz.lily.tool.daogen;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.c.primitive.Primitives;
import net.bodz.bas.codegen.JavaImports;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.cache.Derived;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * Generate java codes for the Builder class.
 */
@ProgramName("foob")
public class FooBuilderCodegen
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(FooBuilderCodegen.class);

    /**
     * Don't use bean properties.
     *
     * @option -B --no-beans
     */
    boolean noBeanProperties = false;

    /**
     * Include public fields.
     *
     * @option -f --fields
     */
    boolean publicFields;

    /**
     * Include all declared fields.
     *
     * @option -a --all
     */
    boolean declaredFields;

    /**
     * Use public static inner class instead of normal class.
     *
     * @option -i --inner
     */
    boolean innerClass;

    boolean useInherit = true;

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (String fqcn : args) {

            Class<?> type;
            try {
                type = Class.forName(fqcn);
                logger.info("Generate builder class for " + type);
            } catch (Throwable e) {
                logger.error("Can't resolve class " + fqcn);
                continue;
            }

            Set<String> parentBuilderProps = new HashSet<>();
            Class<?> parent = type.getSuperclass();
            Class<?> parentBuilderClass = null;
            try {
                parentBuilderClass = Class.forName(parent.getName() + ".Builder");
                for (Method m : parentBuilderClass.getMethods())
                    parentBuilderProps.add(m.getName());
            } catch (Throwable e) {
            }

            String code = generateBuilder(type, parentBuilderClass, parentBuilderProps);
            System.out.println(code);
        }
    }

    boolean includeField(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers))
            return false;
        // if (Modifier.isPrivate(modifiers))
        // return false;
        if (field.getAnnotation(Derived.class) != null)
            return false;

        return true;
    }

    String generateBuilder(Class<?> type, Class<?> parentBuilderClass, Set<String> parentBuilderProps)
            throws IntrospectionException {
        String builderType = innerClass ? "Builder" : type.getSimpleName() + "Builder";

        Map<String, BuilderField> builderFields = new LinkedHashMap<>();

        if (!noBeanProperties) {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
                Method setter = prop.getWriteMethod();
                if (setter == null)
                    continue;
                Class<?> declaringClass = setter.getDeclaringClass();
                if (declaringClass != type)
                    continue;

                String name = prop.getName();
                builderFields.putIfAbsent(name, new BuilderField(builderType, prop, name));
            }
        }

        if (publicFields)
            for (Field field : type.getFields()) {
                if (!includeField(field))
                    continue;
                String name = field.getName();
                builderFields.putIfAbsent(name, new BuilderField(builderType, field, name));
            }

        if (declaredFields)
            for (Field field : type.getDeclaredFields()) {
                if (!includeField(field))
                    continue;
                String name = field.getName();
                builderFields.putIfAbsent(name, new BuilderField(builderType, field, name));
            }

        BCharOut buf = new BCharOut();
        ITreeOut out = buf.indented();
        JavaImports importSet = new JavaImports(type.getPackageName());

        if (innerClass)
            out.enter();

        if (innerClass)
            out.print("public static class " + builderType);
        else
            out.print("public class " + builderType);

        if (parentBuilderClass != null)
            out.print(" extends %s", importSet.name(parentBuilderClass));
        out.println(" {");
        out.enter();

        out.println();
        for (BuilderField f : builderFields.values())
            f.printFieldDecl(out, importSet);

        out.println();
        boolean any = false;
        for (BuilderField f : builderFields.values())
            any |= f.printFieldSpecifiedDecl(out);
        if (any)
            out.println();

        for (BuilderField f : builderFields.values()) {
            out.println();
            f.printMethod(out, importSet);
        }

        out.println();
        out.printf("public %s build() {\n", importSet.name(type));
        out.enter();
        out.printf("%s o = new %s();\n", importSet.name(type), importSet.name(type));
        for (BuilderField f : builderFields.values()) {
            f.printBuildLine(out, importSet);
        }
        out.println("return o;");
        out.leave();
        out.println("}");

        out.println();
        out.leave();
        out.println("}");
        out.flush();
        String body = buf.toString();

        if (innerClass) {
            return body;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("package " + type.getPackageName() + ";\n");
            sb.append("\n");
            for (String fqcn : importSet)
                sb.append("import " + fqcn + ";\n");
            sb.append("\n");
            sb.append(body);
            return sb.toString();
        }
    }

    class BuilderField {

        String builderType;
        Class<?> type;
        String name;
        Field field;
        PropertyDescriptor property;

        public BuilderField(String builderType, PropertyDescriptor property, String name) {
            this.builderType = builderType;
            this.type = property.getPropertyType();
            this.name = name;
            this.property = property;
        }

        public BuilderField(String builderType, Field field, String name) {
            this.builderType = builderType;
            this.type = field.getType();
            this.name = name;
            this.field = field;
        }

        public void printFieldDecl(ITreeOut out, JavaImports im) {
            Class<?> boxed = Primitives.box(type);
            out.printf("%s %s;\n", //
                    im.name(boxed), name);
        }

        public boolean printFieldSpecifiedDecl(ITreeOut out) {
            if (type.isPrimitive())
                // check if specified by testing boxed value with null.
                return false;

            out.printf("boolean %s;\n", //
                    nameSpecified(name));
            return true;
        }

        String nameSpecified(String name) {
            return String.format("_%s_specified", name);
        }

        public void printMethod(ITreeOut out, JavaImports im) {
            String returnType = builderType;
            String methodName = name;
            String paramName = name;
            out.printf("public %s %s(%s %s) {\n", returnType, methodName, //
                    im.name(type), paramName);
            out.enter();
            out.printf("this.%s = %s;\n", name, paramName);
            if (!type.isPrimitive()) {
                String nameSpecified = nameSpecified(name);
                out.printf("this.%s = true;\n", nameSpecified);
            }
            out.println("return this;");
            out.leave();
            out.println("}");
        }

        public void printBuildLine(ITreeOut out, JavaImports im) {
            String specified;
            if (type.isPrimitive()) {
                specified = String.format("this.%s != null", name);
            } else {
                specified = nameSpecified(name);
            }

            if (property != null) {
                Method setter = property.getWriteMethod();
                out.printf("if (%s) o.%s(this.%s);\n", //
                        specified, //
                        setter.getName(), name);
            } else {
                out.printf("if (%s) o.%s = this.%s;\n", //
                        specified, //
                        name, name);
            }
        }

    }

    public static void main(String[] args)
            throws Exception {
        new FooBuilderCodegen().execute(args);
    }

}
