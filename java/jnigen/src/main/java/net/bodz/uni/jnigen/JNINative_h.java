package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class JNINative_h
        extends JNISourceBuilder {

    public JNINative_h(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public File getPreferredFile(SourceFilesForSingleClass sourceFiles) {
        return sourceFiles.jniHeaderFile;
    }

    @Override
    public void buildSource(JNISourceWriter out, File file)
            throws IOException {
        out.println("/** GENERATED FILE, PLEASE DON'T MODIFY. **/");
        out.println();

        out.println("#ifndef __" + uppercasedQualifiedId + "_JNI_H");
        out.println("#define __" + uppercasedQualifiedId + "_JNI_H");
        out.println();

        out.println("#include <jni.h>");
        out.println();

        out.println("#ifdef __cplusplus");
        out.println("extern \"C\" {");
        out.println("#endif");
        out.println();

        for (String methodName : members.getNativeMethodNames()) {
            Map<String, Method> dMap = members.getNativeMethods(methodName);
            for (String dName : dMap.keySet()) {
                out.nativeMethodDecl(dName, dMap.get(dName), "        ", false);
                out.println();
            }
        }

        out.println("#ifdef __cplusplus");
        out.println("}");
        out.println("#endif");

        out.println();
        out.println("#endif");
    }

}
