package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.type.overloaded.ConstructorMap;
import net.bodz.bas.type.overloaded.MethodMap;

public class JNIWrapper_cxx
        extends JNISourceBuilder {

    public JNIWrapper_cxx(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public File getPreferredFile(SourceFilesForSingleClass sourceFiles) {
        return sourceFiles.wrapperSourceFile;
    }

    @Override
    public void buildSource(JNISourceWriter out, File file)
            throws IOException {
        String wrapperHeader = FilePath.getRelativePath(sourceFiles.wrapperHeaderFile, file);
        out.printf("#include \"%s\"\n", wrapperHeader);
        out.println();

        out.printf("using namespace %s;\n", namespace);
        out.println();

        out.printf("thread_local %s_class %s::CLASS;\n", simpleName, simpleName);
        out.println();

        Class<?> parentClass = getInheritParent();

        out.printf("%s::%s(JNIEnv *env)\n", simpleName, simpleName);
        if (clazz.isInterface()) {
            out.printf("        : _env(env) {");
        } else {
            out.printf("        : %s(env) {", parentClass.getSimpleName());
        }
        out.println("}");
        out.println();

        out.printf("%s::%s(JNIEnv *env, jobject jobj)\n", simpleName, simpleName);
        if (clazz.isInterface()) {
            out.printf("        : _env(env), _jobj(jobj) {");
        } else {
            out.printf("        : %s(env, jobj) {", parentClass.getSimpleName());
        }
        out.println("}");
        out.println();

        out.printf("%s *%s::_wrap(jobject jobj) {\n", simpleName, simpleName);
        out.printf("    JNIEnv *env = getEnv();\n");
        out.printf("    return new %s(env, jobj);\n", simpleName);
        out.println("}");
        out.println();

        out.println("/* ctor-create methods */");
        ConstructorMap<?> dCtors = members.getConstructors();
        for (String dName : dCtors.keySet()) {
            ctorDef(out, clazz, dName, dCtors.get(dName));
            out.println();
        }

        Map<String, Field> fields = format.memberOrder.newMap();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers))
                continue;
            fields.put(field.getName(), field);
        }

        String lazyInit = null; // String.format("%s(%s, %s);")
        for (String methodName : members.getMethodNames()) {
            MethodMap dMap = members.getMethods(methodName);
            for (String dName : dMap.keySet()) {
                methodDef(out, clazz, dName, dMap.get(dName), lazyInit);
                out.println();
            }
        }
    }

    void ctorDef(JNISourceWriter out, Class<?> clazz, String ctorName, Constructor<?> ctor) {
        out.ctorDecl(clazz, ctorName, ctor, true);

        if (containsSuperclass()) {
            String parentCtor = clazz.getSuperclass().getSimpleName();
            out.println();
            out.printf("        : %s(getEnv())", parentCtor);
        }
        out.enterln(" {");
        out.printf("jclass jclass = CLASS._class;\n");

        if (!containsSuperclass())
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

    void methodDef(JNISourceWriter out, Class<?> clazz, String methodName, Method method, String _lazyInit) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        out.methodDecl(clazz, methodName, method, true);
        out.enterln("{");

        if (_lazyInit != null)
            out.println(_lazyInit);

        Class<?> retType = method.getReturnType();
        String methodVarName = TypeNames.getMethodVarName(methodName, method);

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

}
