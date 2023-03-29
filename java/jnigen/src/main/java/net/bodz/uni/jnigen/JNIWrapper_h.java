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

        String typeInfoHeader = FilePath.getRelativePath(sourceFiles.typeInfoHeaderFile, file);
        out.printf("#include \"%s\"\n", typeInfoHeader);
        out.println();

        out.printf("namespace %s {\n", namespace);
        out.println();

        out.println("class " + simpleName + " : public IWrapper {");
        out.println("    jobject _this;");
        out.println("    JNIEnv *_env;");
        out.println();

        sect_wrapperCtors(out);

        if (!members.getConstructors().isEmpty())
            sect_initCtors(out);

        if (!members.getConstFieldNames().isEmpty())
            sect_constFields(out);

        if (!members.getFieldNames().isEmpty())
            sect_fieldAccessors(out);

        if (!members.getMethodNames().isEmpty())
            sect_methodWrappers(out);

        out.println("public: ");
        out.printf("    static thread_local %s_class CLASS;\n", simpleName);
        out.println("}; // class " + simpleName);
        out.println();

        out.println("} // namespace");
        out.println();
        out.println("#endif");
    }

    void sect_wrapperCtors(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* wrapper constructor */");
        out.printf("%s(JNIEnv *env, jobject _this);\n", simpleName);
        out.printf("static %s *_wrap(jobject _this);\n", simpleName);
        out.println();
        out.printf("inline JNIEnv *__env() { return _env; }\n");
        out.printf("inline jobject __this() { return _this; }\n");
        out.printf("inline operator jobject() { return _this; }\n");
        out.leaveln("");
    }

    void sect_initCtors(JNISourceWriter out) {
        ConstructorMap<?> dCtors = members.getConstructors();
        out.enterln("public: ");
        out.println("/* ctor-create methods */");
        for (String dName : dCtors.keySet()) {
            out.ctorDecl(dName, dCtors.get(dName), false);
            out.println(";");
        }
        out.leaveln("");
    }

    void sect_constFields(JNISourceWriter out) {
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

    void sect_fieldAccessors(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* field accessors */");
        for (String fieldName : members.getFieldNames()) {
            Field field = members.getField(fieldName);

            String propertyType = propertyType(field.getType());
            out.printf("%s %s = wrapfield(this, CLASS.FIELD_%s);\n", propertyType, field.getName(), fieldName);
        }
        out.leaveln("");
    }

    void sect_methodWrappers(JNISourceWriter out) {
        out.enterln("public: ");
        out.println("/* method wrappers */");
        for (String methodName : members.getMethodNames()) {
            MethodMap dMap = members.getMethods(methodName);
            if (dMap == null)
                throw new NullPointerException("No method: " + methodName);
            for (String dName : dMap.keySet()) {
                out.methodDecl(dName, dMap.get(dName), false);
                out.println(";");
            }
        }
        out.leaveln("");
    }

}
