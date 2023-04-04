package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.type.overloaded.ConstructorMap;
import net.bodz.bas.type.overloaded.MethodMap;

public class JNIWrapper_h
        extends JNISourceBuilder {

    public JNIWrapper_h(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public File getPreferredFile(SourceFilesForSingleClass sourceFiles) {
        return sourceFiles.wrapperHeaderFile;
    }

    @Override
    public void buildSource(JNISourceWriter out, File file)
            throws IOException {
        out.println("/** GENERATED FILE, PLEASE DON'T MODIFY. **/");
        out.println();

        out.println("#ifndef __" + uppercasedQualifiedId + "_H");
        out.println("#define __" + uppercasedQualifiedId + "_H");
        out.println();

        out.println("#include <jni.h>");
        out.println("#include <sweetjni.hxx>");
        out.println();

        if (containsSuperclass()) {
            Class<?> parentClass = getInheritParent();
            String parentFile = parentClass.getName().replace('.', '/');
            String thisDir = clazz.getPackage().getName().replace('.', '/') + "/";
            String parentHref = FilePath.getRelativePath(parentFile, thisDir);
            out.printf("#include \"%s.hxx\"\n", parentHref);
        } else {
            if (!clazz.isInterface())
                out.println("#include <java/lang/Object.hxx>");
        }
        out.println();

        String typeInfoHeader = FilePath.getRelativePath(sourceFiles.typeInfoHeaderFile, file);
        out.printf("#include \"%s\"\n", typeInfoHeader);
        out.println();

        out.printf("namespace %s {\n", namespace);
        out.println();

        if (containsSuperclass()) {
            Class<?> parentClass = getInheritParent();
            String parentClassName = TypeNames.getName(parentClass, true);
            out.printf("class " + simpleName + " : public %s {\n", parentClassName);
        } else {
            if (clazz.isInterface()) {
                out.printf("class " + simpleName + " : public IWrapper {\n");
            } else {
                String parentClassName = TypeNames.getName(Object.class, true);
                out.printf("class " + simpleName + " : public %s {\n", parentClassName);
            }
        }

        if (clazz.isInterface()) {
            out.println("    JNIEnv *_env;");
            if (Throwable.class.isAssignableFrom(clazz))
                out.println("    jthrowable _jobj;");
            else
                out.println("    jobject _jobj;");
            out.println();
        }

        section__WrapperCtors(out);

        if (!members.getConstructors().isEmpty())
            section__InitCtors(out);

        if (!members.getConstFieldNames().isEmpty())
            section__ConstFields(out);

        if (!members.getFieldNames().isEmpty())
            section__FieldAccessors(out);

        if (!members.getMethodNames().isEmpty())
            section__MethodWrappers(out);

        out.println("public: ");
        out.printf("    static thread_local %s_class CLASS;\n", simpleName);
        out.println("}; // class " + simpleName);
        out.println();

        out.println("} // namespace");
        out.println();
        out.println("#endif");
    }

    void section__WrapperCtors(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* wrapper constructor */");
        out.printf("%s(JNIEnv *env);\n", simpleName);
        out.printf("%s(JNIEnv *env, jobject jobj);\n", simpleName);
        out.printf("static %s *_wrap(jobject jobj);\n", simpleName);

        if (clazz.isInterface()) {
            out.println();
            out.printf("inline operator JNIEnv *() const { return _env; }\n");
            out.printf("inline operator jobject() const { return _jobj; }\n");
            if (Throwable.class.isAssignableFrom(clazz))
                out.printf("inline operator jthrowable() const { return _jobj; }\n");
        }

        out.leaveln("");
    }

    void section__InitCtors(JNISourceWriter out) {
        ConstructorMap<?> dCtors = members.getConstructors();
        out.enterln("public: ");
        out.println("/* ctor-create methods */");
        for (String dName : dCtors.keySet()) {
            out.ctorDecl(clazz, dName, dCtors.get(dName), false);
            out.println(";");
        }
        out.leaveln("");
    }

    void section__ConstFields(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* static final basic constant fields */");
        for (String fieldName : members.getConstFieldNames()) {
            Field field = members.getConstField(fieldName);
            String jniType = jniType(field.getType());
            Object value;
            try {
                value = field.get(null);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            String cExpr = quoteC(field.getType(), value, true);
            out.printf("static const %s %s = %s;\n", jniType, field.getName(), cExpr);
        }
        out.leaveln("");
    }

    static String quoteC(Class<?> type, Object val, boolean withComment) {
        switch (TypeKind.getTypeId(type)) {
        case TypeId._byte:
            return "((jbyte) " + (((Byte) val).byteValue() & 0xFF) + ")";
        case TypeId._short:
            return "((jshort) " + ((Short) val).shortValue() + ")";

        case TypeId._int:
            if (withComment) {
                Integer ival = (Integer) val;
                if (ival.intValue() == 0)
                    return "0";
                return String.format("0x%x /* %d */", ival, ival);
            } else {
                return ((Integer) val).toString();
            }

        case TypeId._long:
            if (withComment) {
                Long lval = (Long) val;
                if (lval.longValue() == 0L)
                    return "0";
                return String.format("0x%lx /* %ld */", lval, lval);
            } else {
                return ((Long) val).longValue() + "L";
            }

        case TypeId._float:
            return ((Float) val).floatValue() + "f";
        case TypeId._double:
            return ((Double) val).toString(); // doubleValue() ;
        case TypeId._boolean:
            return ((Boolean) val).toString();
        case TypeId._char:
            String ch1 = ((Character) val).toString();
            return StringQuote.qJavaString(ch1);
        case TypeId.STRING:
            if (val == null)
                return "NULL";
            else
                return StringQuote.qqJavaString(val.toString());
        default:
            throw new IllegalArgumentException("unsupported literal type: " + type);
        }
    }

    void section__FieldAccessors(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* field accessors */");
        for (String fieldName : members.getFieldNames()) {
            Field field = members.getField(fieldName);

            String propertyType = propertyType(field.getType());
            out.printf("%s %s = wrapfield(this, CLASS.FIELD_%s);\n", propertyType, field.getName(), fieldName);
        }
        out.leaveln("");
    }

    void section__MethodWrappers(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* method wrappers */");
        for (String methodName : members.getMethodNames()) {
            MethodMap dMap = members.getMethods(methodName);
            if (dMap == null)
                throw new NullPointerException("No method: " + methodName);
            for (String dName : dMap.keySet()) {
                out.methodDecl(clazz, dName, dMap.get(dName), false);
                out.println(";");
            }
        }
        out.leaveln("");
    }

}
