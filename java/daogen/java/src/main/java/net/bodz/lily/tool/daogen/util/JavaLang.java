package net.bodz.lily.tool.daogen.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.bodz.bas.err.IllegalUsageException;

public class JavaLang {

    static final Set<String> keywords = new HashSet<>();

    static {
        String[] array = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for",
                "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
                "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
                "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", };
        for (String item : array)
            keywords.add(item);
    }

    public static boolean isKeyword(String word) {
        return keywords.contains(word);
    }

    public static String renameKeyword(String name) {
        return renameKeyword(name, (String a) -> false);
    }

    public static String renameKeyword(String name, Predicate<String> existf) {
        if (!isKeyword(name))
            return name;
        String other = name + "_";
        if (!existf.test(other))
            return other;

        for (int i = 1; i <= 19; i++) {
            other = name + "__" + i;
            if (!existf.test(other))
                return other;
        }
        throw new IllegalUsageException(String.format(//
                "can't find an alternative name for '%s' to resolve keyword confliction.", //
                name));
    }

    static final Map<String, String> primitiveMap = new HashMap<>();

    static {
        primitiveMap.put("byte", "B");
        primitiveMap.put("short", "S");
        primitiveMap.put("int", "I");
        primitiveMap.put("long", "J");
        primitiveMap.put("float", "F");
        primitiveMap.put("double", "D");
        primitiveMap.put("char", "C");
        primitiveMap.put("boolean", "Z");
    }

    public static boolean isPrimitive(String typeName) {
        return primitiveMap.containsKey(typeName);
    }

}
