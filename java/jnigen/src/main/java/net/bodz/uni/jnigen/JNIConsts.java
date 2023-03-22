package net.bodz.uni.jnigen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JNIConsts {

    public static Map<Class<?>, String> jniTypeName = new HashMap<>();
    static {
        jniTypeName.put(void.class, "void");

        jniTypeName.put(boolean.class, "jboolean");
        jniTypeName.put(byte.class, "jbyte");
        jniTypeName.put(short.class, "jshort");
        jniTypeName.put(int.class, "jint");
        jniTypeName.put(long.class, "jlong");
        jniTypeName.put(float.class, "jfloat");
        jniTypeName.put(double.class, "jdouble");
        jniTypeName.put(char.class, "jchar");
        jniTypeName.put(String.class, "jstring");
        jniTypeName.put(Class.class, "jclass");

        jniTypeName.put(boolean[].class, "jbooleanArray");
        jniTypeName.put(byte[].class, "jbyteArray");
        jniTypeName.put(short[].class, "jshortArray");
        jniTypeName.put(int[].class, "jintArray");
        jniTypeName.put(long[].class, "jlongArray");
        jniTypeName.put(float[].class, "jfloatArray");
        jniTypeName.put(double[].class, "jdoubleArray");
        jniTypeName.put(char[].class, "jcharArray");
        jniTypeName.put(Object[].class, "jobjectArray");

        jniTypeName.put(Throwable.class, "jthrowable");
    }

    public static Map<Class<?>, String> typeSignature = new HashMap<>();
    static {
        typeSignature.put(void.class, "V");
        // typeSignature.put(Void.class, "V");
        typeSignature.put(boolean.class, "Z");
        typeSignature.put(byte.class, "B");
        typeSignature.put(short.class, "S");
        typeSignature.put(int.class, "I");
        typeSignature.put(long.class, "J");
        typeSignature.put(float.class, "F");
        typeSignature.put(double.class, "D");
        typeSignature.put(char.class, "C");
    }

    public static Map<Class<?>, String> typeCallname = new HashMap<>();
    static {
        typeCallname.put(boolean.class, "Boolean");
        typeCallname.put(byte.class, "Byte");
        typeCallname.put(short.class, "Short");
        typeCallname.put(int.class, "Int");
        typeCallname.put(long.class, "Long");
        typeCallname.put(float.class, "Float");
        typeCallname.put(double.class, "Double");
        typeCallname.put(char.class, "Char");
        // typeCallname.put(String.class, "String");
    }

    public static Set<Class<?>> autoCastTypes = new HashSet<>();
    static {
        autoCastTypes.add(String.class);
        autoCastTypes.add(byte[].class);
        autoCastTypes.add(short[].class);
        autoCastTypes.add(int[].class);
        autoCastTypes.add(long[].class);
        autoCastTypes.add(float[].class);
        autoCastTypes.add(double[].class);
        autoCastTypes.add(char[].class);
        autoCastTypes.add(boolean[].class);
    }

}
