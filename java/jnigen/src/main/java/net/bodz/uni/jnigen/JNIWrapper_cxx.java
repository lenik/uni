package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

        /// if(members.parentClass==null) {
        out.printf("%s::%s(JNIEnv *env, jobject jobj) {\n", simpleName, simpleName);
        out.printf("    this->_env = env;\n");

        String castExpr = "";
        String jniType = jniType(clazz);
        if (!"jobject".equals(jniType))
            castExpr = " (" + jniType + ")";

        out.printf("    this->_jobj =%s jobj;\n", castExpr);
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
            out.ctorDef(clazz, dName, dCtors.get(dName));
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
                out.methodDef(clazz, dName, dMap.get(dName), lazyInit);
                out.println();
            }
        }
    }

}
