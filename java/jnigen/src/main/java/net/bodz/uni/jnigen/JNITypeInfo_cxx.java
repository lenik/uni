package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.type.overloaded.ConstructorMap;
import net.bodz.bas.type.overloaded.MethodMap;

public class JNITypeInfo_cxx
        extends JNISourceBuilder {

    public JNITypeInfo_cxx(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public File getPreferredFile(SourceFilesForSingleClass sourceFiles) {
        return sourceFiles.typeInfoSourceFile;
    }

    @Override
    public void buildSource(JNISourceWriter out, File file)
            throws IOException {
        out.printf("#include <sweetjni.hxx>\n");
        out.println();
        String typeInfoHeader = FilePath.getRelativePath(sourceFiles.typeInfoHeaderFile, file);
        out.printf("#include \"%s\"\n", typeInfoHeader);
        out.println();

        out.printf("using namespace %s;\n", namespace);
        out.println();

        f_init(out);
        out.println();
        f_dump(out);
    }

    void f_init(JNISourceWriter out) {
        ConstructorMap<?> dCtors = members.getConstructors();
        out.printf("%s_class::%s_class()", simpleName, simpleName);

        // default constructor is implied.
        // if (members.parentClass != null)
        // out.printf(" : %s()", members.parentClass.getSimpleName());

        out.enterln(" {");
        {
            out.println("JNIEnv *env = getEnv();");
            out.println("if (env == NULL) return;");

            String jniClassName = clazz.getName().replace('.', '/');
            out.printf("_class = findClass(env, \"%s\");\n", jniClassName);
            out.println("if (_class == NULL) return;");

            for (Field field : members.getFields()) {
                String sig = signature(field.getType());
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers))
                    out.printf("%s = getStaticField(env, _class, \"%s\", \"%s\");\n", //
                            JNISourceWriter.getFieldVarName(field), //
                            field.getName(), sig);
                else
                    out.printf("%s = getField(env, _class, \"%s\", \"%s\");\n", //
                            JNISourceWriter.getFieldVarName(field), //
                            field.getName(), sig);
            }

            /*
             * Constructs a new Java object. The method ID indicates which constructor method to invoke. This ID must be
             * obtained by calling GetMethodID() with <init> as the method name and void (V) as the return type.
             */
            for (String dName : dCtors.keySet()) {
                Constructor<?> ctor = dCtors.get(dName);
                String sig = signature(ctor);
                out.printf("%s = getMethod(env, _class, \"<init>\", \"%s\");\n", //
                        TypeNames.getCtorVarName(dName, ctor), //
                        sig);
            }

            for (String name : members.getMethodNames()) {
                MethodMap dMap = members.getMethods(name);
                for (String dName : dMap.keySet()) {
                    Method method = dMap.get(dName);
                    String sig = signature(method);
                    int modifiers = method.getModifiers();
                    if (Modifier.isStatic(modifiers))
                        out.printf("%s = getStaticMethod(env, _class, \"%s\", \"%s\");\n", //
                                TypeNames.getMethodVarName(dName, method), //
                                name, sig);
                    else
                        out.printf("%s = getMethod(env, _class, \"%s\", \"%s\");\n", //
                                TypeNames.getMethodVarName(dName, method), //
                                name, sig);
                }
            }
            out.leaveln("}");
        }
    }

    void f_dump(JNISourceWriter out) {
        ConstructorMap<?> dCtors = members.getConstructors();

        out.printf("void " + simpleName + "_class::dump()");
        out.enterln(" {");
        {
            // super.dump();

            for (Field field : members.getFields()) {
                int modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers))
                    continue;
                String var = JNISourceWriter.getFieldVarName(field);
                out.printf("printf(\"%s: %%p\\n\", %s.id);\n", var, var);
            }

            for (String ctorName : dCtors.keySet()) {
                Constructor<?> ctor = dCtors.get(ctorName);
                String var = TypeNames.getCtorVarName(ctorName, ctor);
                out.printf("printf(\"%s: %%p\\n\", %s.id);\n", var, var);
            }

            for (String name : members.getMethodNames()) {
                MethodMap dMap = members.getMethods(name);
                for (String dName : dMap.keySet()) {
                    Method method = dMap.get(dName);
                    String var = TypeNames.getMethodVarName(dName, method);
                    out.printf("printf(\"%s: %%p\\n\", %s.id);\n", var, var);
                }
            }
            out.leaveln("}");
        }
    }

}
