package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.bodz.bas.type.overloaded.ConstructorMap;

public class JNITypeInfo_h
        extends JNISourceBuilder {

    public JNITypeInfo_h(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public File getPreferredFile(SourceFilesForSingleClass sourceFiles) {
        return sourceFiles.typeInfoHeaderFile;
    }

    @Override
    public void buildSource(JNISourceWriter out, File file)
            throws IOException {
        out.println("/** GENERATED FILE, PLEASE DON'T MODIFY. **/");
        out.println();

        ConstructorMap<?> dCtors = members.getConstructors();

        out.println("#ifndef __" + uppercasedQualifiedId + "_CLASS_H");
        out.println("#define __" + uppercasedQualifiedId + "_CLASS_H");
        out.println();

        out.println("#include <jni.h>");
        out.println();

        out.printf("namespace %s {\n", namespace);
        out.println();

        if (members.parentClass == null)
            out.printf("class %s_class {\n", simpleName);
        else {
            String parent = TypeNames.getName(members.parentClass, true);
            out.printf("class %s_class : public %s_class {\n", simpleName, parent);
        }

        out.println("public:");
        out.printf("    %s_class();\n", simpleName);
        out.println("    void dump();");
        out.println();

        out.enterln("public:");
        {
            if (members.parentClass == null)
                out.println("jclass _class;");

            int n = 0;
            for (Field field : members.getFields()) {
                if (n++ == 0)
                    out.println();
                out.fieldVarDecl(clazz, field, false);
                out.println(";");
            }

            n = 0;
            for (String dName : dCtors.keySet()) {
                if (n++ == 0)
                    out.println();
                out.ctorVarDecl(dName, dCtors.get(dName), false);
                out.println(";");
            }

            n = 0;
            for (String methodName : members.getMethodNames()) {
                Map<String, Method> dMap = members.getMethods(methodName);
                for (String dName : dMap.keySet()) {
                    if (n++ == 0)
                        out.println();
                    out.methodVarDecl(dName, dMap.get(dName), false);
                    out.println(";");
                }
            }
            out.leave();
        }

        out.printf("}; // class %s_class\n", simpleName);
        out.println();

        out.println("} // namespace");
        out.println();
        out.println("#endif");
    }

}
