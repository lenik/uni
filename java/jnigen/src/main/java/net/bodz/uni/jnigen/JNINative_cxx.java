package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;

public class JNINative_cxx
        extends JNISourceBuilder {

    File wrapperHeaderFile;

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
        String wrapperHeader = FilePath.getRelativePath(wrapperHeaderFile, file);
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
                out.nativeMethodDef(dName, dMap.get(dName));
                out.println();
            }
        }

        out.println("#ifdef __cplusplus");
        out.println("}");
        out.println("#endif");
    }

}
