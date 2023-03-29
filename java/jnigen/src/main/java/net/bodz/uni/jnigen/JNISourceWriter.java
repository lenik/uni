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

    public void ctorDecl(String ctorName, Constructor<?> ctor, boolean def) {
        Class<?> type = ctor.getDeclaringClass();

        if (def) {
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }

        // out.print(ctorName);
        out.print(type.getSimpleName());
        out.print("(");
        paramsDecl(ctor.getParameters());
        out.print(")");
        throwsDecl(ctor.getExceptionTypes());
    }

    void ctorDef(String ctorName, Constructor<?> ctor) {
        ctorDecl(ctorName, ctor, true);

        out.enterln("{");
        out.printf("jclass clazz = CLASS._class;\n");
        out.printf("this->_env = getEnv();\n");
        out.printf("this->_this = newObject(_env, clazz, CLASS.INIT%s", ctorName);

        for (Parameter param : ctor.getParameters()) {
            out.print(", ");
            out.print(param.getName());
        }
        out.println(");");
        out.leaveln("}");
    }

    void methodDecl(String methodName, Method method, boolean def) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        if (isStatic)
            if (!def)
                out.print("static ");

        out.print(jniType(method.getReturnType()));
        out.print(" ");

        if (def) {
            Class<?> type = method.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }

        out.print(methodName);
        out.print("(");
        paramsDecl(method.getParameters());
        out.print(")");
        throwsDecl(method.getExceptionTypes());
    }

    void methodDef(String methodName, Method method, String _lazyInit) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        methodDecl(methodName, method, true);
        out.enterln("{");

        if (_lazyInit != null)
            out.println(_lazyInit);

        Class<?> retType = method.getReturnType();
        String methodId = getMethodIdVar(methodName, method);

        StringBuilder callExpr = new StringBuilder();
        if (isStatic) {
            out.println("JNIEnv *env = getEnv();");
            String callMethodFunc = callStaticMethodFunc(method.getReturnType());
            callExpr.append(String.format("env->%s(CLASS._class, CLASS.%s", //
                    callMethodFunc, methodId));
        } else {
            String callMethodFunc = callMethodFunc(method.getReturnType());
            callExpr.append(String.format("_env->%s(_this, CLASS.%s", //
                    callMethodFunc, methodId));
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

    static String getFieldIdVar(Field field) {
        return "FIELD_" + field.getName();
    }

    void fieldIdDecl(Field field, boolean def) {
        out.print("jfieldID ");
        if (def) {
            Class<?> type = field.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(getFieldIdVar(field));
    }

    void fieldIdDef(Field field) {
        fieldIdDecl(field, true);
        out.println(";");
    }

    static String getCtorIdVar(String ctorName, Constructor<?> ctor) {
        return "INIT" + ctorName;
    }

    void ctorIdDecl(String ctorName, Constructor<?> ctor, boolean def) {
        out.print("jmethodID ");
        if (def) {
            Class<?> type = ctor.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(getCtorIdVar(ctorName, ctor));
    }

    void ctorIdDef(String ctorName, Constructor<?> ctor) {
        ctorIdDecl(ctorName, ctor, true);
        out.println(";");
    }

    static String getMethodIdVar(String methodName, Method method) {
        return "METHOD_" + methodName;
    }

    void methodIdDecl(String methodName, Method method, boolean def) {
        out.print("jmethodID ");
        if (def) {
            Class<?> type = method.getDeclaringClass();
            String c = type.getSimpleName();
            out.print(c);
            out.print("::");
        }
        out.print(getMethodIdVar(methodName, method));
    }

    void methodIdDef(String methodName, Method method) {
        methodIdDecl(methodName, method, true);
        out.println(";");
    }

    void nativeMethodDecl(String methodName, Method method, String softIndent, boolean def) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        Class<?> clazz = method.getDeclaringClass();
        String jniClassName = clazz.getName().replace('.', '_');
        out.println("/*");
        out.println(" * Class: " + jniClassName);
        out.println(" * Method: " + method.getName());
        out.println(" * Signature: " + signature(method));
        out.println(" */");

        String jniFunctionName = "Java_" + jniClassName + "_" + method.getName();
        out.printf("JNIEXPORT %s JNICALL %s", jniType(method.getReturnType()), jniFunctionName);
        if (softIndent != null) {
            out.print("\n");
            out.print(softIndent);
        }

        if (isStatic)
            out.print("(JNIEnv *env, jclass _class");
        else
            out.print("(JNIEnv *env, jobject _this");

        if (method.getParameterCount() != 0) {
            out.print(", ");
            paramsDecl(method.getParameters());
        }
        out.print(")");
        // throwsDecl(method.getExceptionTypes());
    }

    void nativeMethodDef(String methodName, Method method) {
        nativeMethodDecl(methodName, method, "        ", true);
        out.enterln(" {");
        Class<?> returnType = method.getReturnType();
        if (returnType != void.class)
            if (returnType.isPrimitive()) {
                out.println("return 0;");
            } else {
                out.println("return NULL;");
            }
        out.leaveln("}");
    }

}
