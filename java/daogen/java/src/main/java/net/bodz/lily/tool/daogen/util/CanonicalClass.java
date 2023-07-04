package net.bodz.lily.tool.daogen.util;

public class CanonicalClass {

    public static Class<?> forName(String canonicalName)
            throws ClassNotFoundException {
        String className = decorateName(canonicalName);
        return Class.forName(className);
    }

    static String decorateName(String canonicalClassName) {
        if (canonicalClassName.endsWith("[]")) {
            String componentType = canonicalClassName.substring(0, canonicalClassName.length() - 2);
            componentType = componentType.trim();
            String decorated = JavaLang.primitiveMap.get(componentType);
            if (decorated != null)
                return "[" + decorated;
            decorated = decorateName(componentType);
            if (decorated.startsWith("["))
                return "[" + decorated;
            else
                return "[L" + decorated + ";";
        }
        return canonicalClassName;
    }

}
