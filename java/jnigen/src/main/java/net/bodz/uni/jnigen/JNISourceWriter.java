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

    void ctorDef(Class<?> clazz, String ctorName, Constructor<?> ctor) {
        ctorDecl(clazz, ctorName, ctor, true);

        out.enterln("{");
        out.printf("jclass jclass = CLASS._class;\n");
        out.printf("this->_env = getEnv();\n");

        String castExpr = "";
        String jniType = jniType(clazz);
        if (!"jobject".equals(jniType))
            castExpr = " (" + jniType + ")";

        out.printf("this->_jobj =%s newObject(_env, CLASS.INIT%s", castExpr, ctorName);

        for (Parameter param : ctor.getParameters()) {
            out.print(", ");
            out.print(param.getName());
        }
        out.println(");");
        out.leaveln("}");
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

    void methodDef(Class<?> clazz, String methodName, Method method, String _lazyInit) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        methodDecl(clazz, methodName, method, true);
        out.enterln("{");

        if (_lazyInit != null)
            out.println(_lazyInit);

        Class<?> retType = method.getReturnType();
        String methodVarName = getMethodVarName(methodName, method);

        StringBuilder callExpr = new StringBuilder();
        if (isStatic) {
            out.println("JNIEnv *env = getEnv();");
            String callMethodFunc = callStaticMethodFunc(method.getReturnType());
            callExpr.append(String.format("env->%s(CLASS._class, CLASS.%s.id", //
                    callMethodFunc, methodVarName));
        } else {
            String callMethodFunc = callMethodFunc(method.getReturnType());
            callExpr.append(String.format("_env->%s(_jobj, CLASS.%s.id", //
                    callMethodFunc, methodVarName));
        }

        for (Parameter param : method.getParameters()) {
            callExpr.append(", ");
            // Class<?> paramType = param.getType();
            // String cParamType = jniType(paramType);
            callExpr.append(param.getName());
        }
        callExpr.append(")");
        String expr = callExpr.toString();

        if (retType != void.class) {
            out.print(jniType(retType) + " ret = ");
            expr = autoCastFromObject(retType, callExpr.toString());
        }

        out.print(expr);
        out.println(";");

        if (retType != void.class)
            out.println("return ret;");

        out.leaveln("}");
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

    static String getCtorVarName(String ctorName, Constructor<?> ctor) {
        return "INIT" + ctorName;
    }

    void ctorVarDecl(String ctorName, Constructor<?> ctor, boolean def) {
        out.print("jmethod ");
        if (def) {
            Class<?> type = ctor.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(getCtorVarName(ctorName, ctor));
    }

    void ctorVarDef(String ctorName, Constructor<?> ctor) {
        ctorVarDecl(ctorName, ctor, true);
        out.println(";");
    }

    static String getMethodVarName(String methodName, Method method) {
        return "METHOD_" + methodName;
    }

    void methodVarDecl(String methodName, Method method, boolean def) {
        out.print("jmethod ");
        if (def) {
            Class<?> type = method.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(getMethodVarName(methodName, method));
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
