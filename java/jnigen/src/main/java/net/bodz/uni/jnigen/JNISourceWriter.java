package net.bodz.uni.jnigen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import net.bodz.bas.io.DecoratedTreeOut;
import net.bodz.bas.io.ITreeOut;

public class JNISourceWriter
        extends DecoratedTreeOut
        implements
            JNIAware {

    private static final long serialVersionUID = 1L;

    ITreeOut out;

    public JNISourceWriter(ITreeOut out) {
        super(out);
        this.out = out;
    }

    public void ctorDecl(Class<?> clazz, String ctorName, Constructor<?> ctor, boolean def) {
        if (def) {
            String c = clazz.getSimpleName();
            out.print(c);
            out.print("::");
        }

        // out.print(ctorName);
        out.print(clazz.getSimpleName());
        out.print("(");
        paramsDecl(ctor.getParameters());
        out.print(")");
        throwsDecl(ctor.getExceptionTypes());
    }

    void methodDecl(Class<?> clazz, String methodName, Method method, boolean def) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        if (isStatic)
            if (!def)
                out.print("static ");

        out.print(jniType(method.getReturnType()));
        out.print(" ");

        if (def) {
            String c = clazz.getSimpleName();
            out.print(c);
            out.print("::");
        }

        out.print(methodName);
        out.print("(");
        paramsDecl(method.getParameters());
        out.print(")");
        throwsDecl(method.getExceptionTypes());
    }

    void paramsDecl(Parameter[] params) {
        int i = 0;
        for (Parameter param : params) {
            String name = param.getName();
            Class<?> type = param.getType();
            String cType = jniType(type);
            if (i++ != 0)
                out.print(", ");
            out.print(cType + " " + name);
        }
    }

    void throwsDecl(Class<?>[] exceptionTypes) {
        if (exceptionTypes.length != 0) {
        }
    }

    static String getFieldVarName(Field field) {
        return "FIELD_" + field.getName();
    }

    void fieldVarDecl(Class<?> clazz, Field field, boolean def) {
        out.print("jfield ");
        if (def) {
            String c = clazz.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(getFieldVarName(field));
    }

    void fieldVarDef(Class<?> clazz, Field field) {
        fieldVarDecl(clazz, field, true);
        out.println(";");
    }

    void ctorVarDecl(String ctorName, Constructor<?> ctor, boolean def) {
        out.print("jmethod ");
        if (def) {
            Class<?> type = ctor.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(TypeNames.getCtorVarName(ctorName, ctor));
    }

    void ctorVarDef(String ctorName, Constructor<?> ctor) {
        ctorVarDecl(ctorName, ctor, true);
        out.println(";");
    }

    void methodVarDecl(String methodName, Method method, boolean def) {
        out.print("jmethod ");
        if (def) {
            Class<?> type = method.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(TypeNames.getMethodVarName(methodName, method));
    }

    void methodVarDef(String methodName, Method method) {
        methodVarDecl(methodName, method, true);
        out.println(";");
    }

    void nativeMethodDecl(String methodName, Method method, String softIndent, boolean def) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        Class<?> clazz = method.getDeclaringClass();
        String jniClassName = jniClassName(clazz);
        out.println("/*");
        out.println(" * Class: " + jniClassName);
        out.println(" * Method: " + method.getName());
        out.println(" * Signature: " + signature(method));
        out.println(" */");

        String jniFunctionName = jniFunctionName(method);
        out.printf("JNIEXPORT %s JNICALL %s", //
                jniType(method.getReturnType()), jniFunctionName);
        if (softIndent != null) {
            out.print("\n");
            out.print(softIndent);
        }

        if (isStatic)
            out.print("(JNIEnv *env, jclass jclass");
        else
            out.print("(JNIEnv *env, jobject jobj");

        if (method.getParameterCount() != 0) {
            out.print(", ");
            paramsDecl(method.getParameters());
        }
        out.print(")");
        // throwsDecl(method.getExceptionTypes());
    }

}
