package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;

public class JNINative_cxx
        extends JNISourceBuilder {

    boolean prepareParameters = true;

    public JNINative_cxx(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public File getPreferredFile(SourceFilesForSingleClass sourceFiles) {
        return sourceFiles.jniSourceFile;
    }

    @Override
    public void buildSource(JNISourceWriter out, File file)
            throws IOException {
        out.printf("#include <sweetjni.hxx>\n");
        out.println();
        String jniHeader = FilePath.getRelativePath(sourceFiles.jniHeaderFile, file);
        String wrapperHeader = FilePath.getRelativePath(sourceFiles.wrapperHeaderFile, file);
        String typeInfoHeader = FilePath.getRelativePath(sourceFiles.typeInfoHeaderFile, file);
        out.printf("#include \"%s\"\n", jniHeader);
        out.printf("#include \"%s\"\n", wrapperHeader);
        out.printf("#include \"%s\"\n", typeInfoHeader);
        out.println();

        String ns = clazz.getPackage().getName().replace(".", "::");
        out.printf("using namespace %s;\n", ns);
        out.println();

        out.println("#ifdef __cplusplus");
        out.println("extern \"C\" {");
        out.println("#endif");
        out.println();

        for (String methodName : members.getNativeMethodNames()) {
            Map<String, Method> dMap = members.getNativeMethods(methodName);
            for (String dName : dMap.keySet()) {
                nativeMethodDef(out, dName, dMap.get(dName));
                out.println();
            }
        }

        out.println("#ifdef __cplusplus");
        out.println("}");
        out.println("#endif");
    }

    void nativeMethodDef(JNISourceWriter out, String methodName, Method method) {
        out.nativeMethodDecl(methodName, method, "        ", true);
        out.enterln(" {");

        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);

        if (!isStatic) {
            Class<?> declaringClass = method.getDeclaringClass();
            String wrapperClass = declaringClass.getSimpleName();
            out.printf("%s wrapper(env, jobj), *_this = &wrapper;\n", wrapperClass);
        }

        Parameter[] parameters = method.getParameters();
        if (prepareParameters) { // parameters <open>
            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                String name = param.getName();
                Class<?> type = param.getType();

                if (type == String.class) {
                    out.printf("const char *%s_chars = env->GetStringUTFChars(%s, NULL);\n", //
                            name, name);
                    continue;
                }

                if (type.isArray()) {
                    Class<?> type1 = type.getComponentType();
                    if (type1.isPrimitive()) {
                        String nativeType1 = jniType(type1);
                        String callType = _callType(type1);
                        out.printf("%s *%s_elements = env->Get%sArrayElements(%s, NULL);\n", //
                                nativeType1, name, callType, name);
                        continue;
                    }
                }
            }
        }

        out.println("// TODO");

        if (prepareParameters) { // parameters <close>
            for (int i = parameters.length - 1; i >= 0; i--) {
                Parameter param = parameters[i];
                String name = param.getName();
                Class<?> type = param.getType();

                if (type == String.class) {
                    out.printf("env->ReleaseStringUTFChars(%s, %s_chars);\n", //
                            name, name);
                    continue;
                }

                if (type.isArray()) {
                    Class<?> type1 = type.getComponentType();
                    if (type1.isPrimitive()) {
                        String callType = _callType(type1);
                        String modeExpr = "0"; // release and copy back.
                        out.printf("env->Release%sArrayElements(%s, %s_elements, %s);\n", //
                                callType, name, name, modeExpr);
                        continue;
                    }
                }
            }
        }

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
