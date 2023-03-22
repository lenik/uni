package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.loader.scan.ClassScanner;
import net.bodz.bas.c.loader.scan.TypeCollector;
import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.system.SystemProperties;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.repr.form.SortOrder;

/**
 * Generate C++ helpers for JNI classes.
 */
@ProgramName("jnigen")
public class JNIWrapperCodeGen
        extends BasicCLI
        implements
            JNIAware {

    ClassLoader loader;
    ClassScanner scanner;
    TypeCollector collector;

    /**
     * Scan classes within this package. (and all subpackages)
     *
     * @option -P --package =NAME
     */
    String scanPackage;

    /**
     * Sort members.
     *
     * @option -s
     */
    boolean sortMembers;
    SortOrder memberOrder = SortOrder.KEEP;

    /**
     * Specify the output directory.
     *
     * By default, files are saved in src/main/native/ for maven project, or src/ otherwise.
     *
     * @option -O --outdir =DIR
     */
    File outDir;
    static final String MAVEN_DIR = "src/main/native";

    /**
     * Just print to stdout, instead of creating files.
     *
     * @option -c
     */
    boolean stdout;

    /**
     * Use flatten file names. By default, files are organized by directories according to package names.
     *
     * @option -f
     */
    boolean flatten;

    String headerExtension = ".hxx";
    String sourceExtension = ".cxx";

    public JNIWrapperCodeGen() {
        loader = getClass().getClassLoader();
        scanner = new ClassScanner(loader);

        if (outDir == null) {
            String workDir = SystemProperties.getUserDir();
            MavenPomDir pomDir = MavenPomDir.closest(workDir);
            if (pomDir != null)
                outDir = new File(pomDir.getBaseDir(), MAVEN_DIR);
            else
                outDir = new File("src");
        }
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {

        if (scanPackage == null)
            throw new IllegalArgumentException("Package name isn't specified.");

        if (sortMembers)
            memberOrder = SortOrder.SORTED;

        List<Class<?>> list = scanner.scanPackage(scanPackage);

        for (Class<?> clazz : list) {
            String packageName = clazz.getPackage().getName();
            String name = clazz.getSimpleName();
            String headerFileName;
            String sourceFileName;
            if (flatten) {
                String packagePrefix = packageName.replace('.', '_');
                headerFileName = packagePrefix + "_" + name + headerExtension;
                sourceFileName = packagePrefix + "_" + name + sourceExtension;
            } else {
                String packageDir = packageName.replace('.', '/');
                headerFileName = packageDir + "/" + name + headerExtension;
                sourceFileName = packageDir + "/" + name + sourceExtension;
            }
            File headerFile = new File(outDir, headerFileName);
            File sourceFile = new File(outDir, sourceFileName);
            // File libraryHeaderFile = new File(outDir, "jnigen" + headerExtension);
            // String libraryHeaderHref = FilePath.getRelativePath(libraryHeaderFile, headerFile);

            ITreeOut out = Stdio.cout.indented();
            if (!stdout)
                out = openAsIndendted(headerFile);

            JNISourceWriter jsw = new JNISourceWriter(out);

            OverloadedCtors ctors = new OverloadedCtors();
            for (Constructor<?> ctor : clazz.getConstructors())
                ctors.add(ctor);

            Map<String, OverloadedMethods> methodNameMap = memberOrder.newMap();
            for (Method method : clazz.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers))
                    continue;
                String methodName = method.getName();
                OverloadedMethods methods = methodNameMap.get(methodName);
                if (methods == null) {
                    methods = new OverloadedMethods();
                    methodNameMap.put(methodName, methods);
                }
                methods.add(method);
            }

            if (stdout)
                jsw.println("/** FILE: " + headerFileName + " */");
            generateWrapperHeader(jsw, clazz, ctors, methodNameMap);
            jsw.close();

            if (!stdout)
                out = openAsIndendted(sourceFile);
            jsw = new JNISourceWriter(out);

            if (stdout)
                jsw.println("/** FILE: " + sourceFileName + " */");
            generateWrapperImpl(jsw, clazz, ctors, methodNameMap);
            jsw.close();
        }

    }

    static ITreeOut openAsIndendted(File file)
            throws IOException {
        File parent = file.getParentFile();
        parent.mkdirs();
        if (!parent.exists() || !parent.isDirectory())
            throw new IOException("Can't create parent directory: " + parent);
        return new FileResource(file).newTreeOut();
    }

    void generateWrapperHeader(JNISourceWriter out, Class<?> clazz, //
            OverloadedCtors ctors, Map<String, OverloadedMethods> methodNameMap) {
        out.println("/** GENERATED FILE, PLEASE DON'T MODIFY. **/");
        out.println();

        String Q_NAME = StringId.UL.breakQCamel(clazz.getName());
        Q_NAME = Q_NAME.replace('.', '_').toUpperCase();

        out.println("#ifndef __" + Q_NAME + "_H");
        out.println("#define __" + Q_NAME + "_H");
        out.println();

        out.println("#include <jni.h>");
        out.println("#include <jnigen.hxx>");
        out.println();

        String ns = clazz.getPackage().getName().replace(".", "::");
        out.printf("namespace %s {\n", ns);
        out.println();

        Map<String, Field> fieldMap = memberOrder.newMap();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers))
                continue;
            fieldMap.put(field.getName(), field);
        }

        String cClass = clazz.getSimpleName();

        out.printf("class %s_class {\n", cClass);
        out.println("public:");
        out.printf("    %s_class();\n", cClass);
        out.println("    void dump();");
        out.println();

        out.enterln("public:");
        {
            out.println("jclass _class;");

            int n = 0;
            for (Field field : fieldMap.values()) {
                if (n++ == 0)
                    out.println();
                out.fieldIdDecl(field, false);
                out.println(";");
            }

            n = 0;
            Map<String, Constructor<?>> ctorMap = ctors.distinguishablization();
            for (String ctorName : ctorMap.keySet()) {
                if (n++ == 0)
                    out.println();
                out.ctorIdDecl(ctorName, ctorMap.get(ctorName), false);
                out.println(";");
            }

            n = 0;
            for (String methodName : methodNameMap.keySet()) {
                OverloadedMethods methods = methodNameMap.get(methodName);
                Map<String, Method> methodMap = methods.distinguishablization();
                for (String qMethodName : methodMap.keySet()) {
                    if (n++ == 0)
                        out.println();
                    out.methodIdDecl(qMethodName, methodMap.get(qMethodName), false);
                    out.println(";");
                }
            }
            out.leave();
        }

        out.printf("}; // class %s_class\n", cClass);
        out.println();

        out.println("class " + cClass + " : public IWrapper {");
        out.println("    jobject _this;");
        out.println("    JNIEnv *_env;");
        out.println();
        out.enterln("public: ");
        {
            out.println("/* wrapper constructor */");
            out.printf("%s(JNIEnv *env, jobject _this);\n", cClass);
            out.printf("static %s *_wrap(jobject _this);\n", cClass);
            out.println();
            out.printf("inline JNIEnv *__env() { return _env; }\n");
            out.printf("inline jobject __this() { return _this; }\n");
            out.leaveln("");
        }

        out.enterln("public: ");
        {
            out.println("/* ctor-create methods */");
            Map<String, Constructor<?>> ctorMap = ctors.distinguishablization();
            for (String ctorName : ctorMap.keySet()) {
                out.ctorDecl(ctorName, ctorMap.get(ctorName), false);
                out.println(";");
            }
            out.leaveln("");
        }

        out.enterln("public: ");
        {
            out.println("/* field accessors */");
            for (String fieldName : fieldMap.keySet()) {
                Field field = fieldMap.get(fieldName);
                String propertyType = propertyType(field.getType());
                out.printf("%s %s = wrapfield(this, CLASS.FIELD_%s);\n", propertyType, field.getName(), fieldName);
            }
            out.leaveln("");
        }

        out.enterln("public: ");
        {
            out.println("/* method wrappers */");
            for (String methodName : methodNameMap.keySet()) {
                OverloadedMethods methods = methodNameMap.get(methodName);
                Map<String, Method> methodMap = methods.distinguishablization();
                if (methodMap == null)
                    throw new NullPointerException("methodMap: " + methodName);

                for (String qMethodName : methodMap.keySet()) {
                    out.methodDecl(qMethodName, methodMap.get(qMethodName), false);
                    out.println(";");
                }
            }
            out.leaveln("");
        }

        out.println("public: ");
        out.printf("    static thread_local %s_class CLASS;\n", cClass);
        out.println("}; // class " + cClass);
        out.println();

        out.println("} // namespace");
        out.println();
        out.println("#endif");
    }

    void generateWrapperImpl(JNISourceWriter out, Class<?> clazz, OverloadedCtors ctors,
            Map<String, OverloadedMethods> methodNameMap) {
        String headerFileName = clazz.getSimpleName() + headerExtension;
        out.printf("#include \"%s\"\n", headerFileName);
        out.println();

        String ns = clazz.getPackage().getName().replace(".", "::");
        out.printf("using namespace %s;\n", ns);
        out.println();

        String Q_NAME = StringId.UL.breakQCamel(clazz.getName());
        Q_NAME = Q_NAME.replace('.', '_').toUpperCase();

        String cClass = clazz.getSimpleName();

        out.printf("%s::%s(JNIEnv *env, jobject _this) {\n", cClass, cClass);
        out.printf("    this->_this = _this;\n");
        out.printf("    this->_env = env;\n");
        out.println("}");
        out.println();

        out.printf("%s *%s::_wrap(jobject _this) {\n", cClass, cClass);
        out.printf("    JNIEnv *env = getEnv();\n");
        out.printf("    return new %s(env, _this);\n", cClass);
        out.println("}");
        out.println();

        out.println("/* ctor-create methods */");
        Map<String, Constructor<?>> ctorMap = ctors.distinguishablization();
        for (String ctorName : ctorMap.keySet()) {
            out.ctorDef(ctorName, ctorMap.get(ctorName));
            out.println();
        }

        Map<String, Field> fields = memberOrder.newMap();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers))
                continue;
            fields.put(field.getName(), field);
        }

        String lazyInit = null; // String.format("%s(%s, %s);")
        for (String methodName : methodNameMap.keySet()) {
            OverloadedMethods methods = methodNameMap.get(methodName);
            Map<String, Method> methodMap = methods.distinguishablization();
            for (String qMethodName : methodMap.keySet()) {
                out.methodDef(qMethodName, methodMap.get(qMethodName), lazyInit);
                out.println();
            }
        }

        out.printf("thread_local %s_class %s::CLASS;\n", cClass, cClass);
        out.println();

        out.printf("%s_class::%s_class()", cClass, cClass);
        out.enterln(" {");
        {
            out.println("JNIEnv *env = getEnv();");
            out.println("if (env == NULL) return;");

            String jniClassName = clazz.getName().replace('.', '/');
            out.printf("_class = findClass(env, \"%s\");\n", jniClassName);
            out.println("if (_class == NULL) return;");

            for (Field field : fields.values()) {
                String sig = signature(field.getType());
                out.printf("%s = env->GetFieldID(_class, \"%s\", \"%s\");\n", //
                        JNISourceWriter.getFieldIdVar(field), //
                        field.getName(), sig);
            }

            /*
             * Constructs a new Java object. The method ID indicates which constructor method to invoke. This ID must be
             * obtained by calling GetMethodID() with <init> as the method name and void (V) as the return type.
             */
            for (String ctorName : ctorMap.keySet()) {
                Constructor<?> ctor = ctorMap.get(ctorName);
                String sig = signature(ctor);
                out.printf("%s = env->GetMethodID(_class, \"<init>\", \"%s\");\n", //
                        JNISourceWriter.getCtorIdVar(ctorName, ctor), //
                        sig);
            }

            for (String methodName : methodNameMap.keySet()) {
                OverloadedMethods methods = methodNameMap.get(methodName);
                Map<String, Method> methodMap = methods.distinguishablization();
                for (String qMethodName : methodMap.keySet()) {
                    Method method = methodMap.get(qMethodName);
                    String sig = signature(method);
                    int modifiers = method.getModifiers();
                    if (Modifier.isStatic(modifiers))
                        out.printf("%s = env->GetStaticMethodID(_class, \"%s\", \"%s\");\n", //
                                JNISourceWriter.getMethodIdVar(qMethodName, method), //
                                methodName, sig);
                    else
                        out.printf("%s = env->GetMethodID(_class, \"%s\", \"%s\");\n", //
                                JNISourceWriter.getMethodIdVar(qMethodName, method), //
                                methodName, sig);
                }
            }
            out.leaveln("}");
        }
        out.println();

        out.printf("void " + cClass + "_class::dump()");
        out.enterln(" {");
        {
            for (Field field : fields.values()) {
                int modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers))
                    continue;
                String var = JNISourceWriter.getFieldIdVar(field);
                out.printf("printf(\"%s: %%d\\n\", %s);\n", var, var);
            }

            for (String ctorName : ctorMap.keySet()) {
                Constructor<?> ctor = ctorMap.get(ctorName);
                String var = JNISourceWriter.getCtorIdVar(ctorName, ctor);
                out.printf("printf(\"%s: %%d\\n\", %s);\n", var, var);
            }

            for (String methodName : methodNameMap.keySet()) {
                OverloadedMethods methods = methodNameMap.get(methodName);
                Map<String, Method> methodMap = methods.distinguishablization();
                for (String qMethodName : methodMap.keySet()) {
                    Method method = methodMap.get(qMethodName);
                    String var = JNISourceWriter.getMethodIdVar(qMethodName, method);
                    out.printf("printf(\"%s: %%d\\n\", %s);\n", var, var);
                }
            }
            out.leaveln("}");
        }
    }

    public static void main(String[] args)
            throws Exception {
        new JNIWrapperCodeGen().execute(args);
    }

}
