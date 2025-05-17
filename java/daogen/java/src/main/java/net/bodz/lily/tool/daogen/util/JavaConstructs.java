package net.bodz.lily.tool.daogen.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.bodz.bas.c.string.StringQuote;

public class JavaConstructs {

    public static boolean isStringCtorPresent(Class<?> type) {
        try {
            type.getConstructor(String.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static JavaConstructor<Object> stringCtor(Class<?> type) {
        return stringCtor(type, false);
    }

    public static JavaConstructor<Object> stringCtor(Class<?> type, boolean raiseError) {
        try {
            type.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            if (raiseError)
                throw new IllegalArgumentException("class doesn't have a single string constructor: " + type);
            else
                return null;
        }

        return (val, ctx) -> {
            if (val == null)
                return "null";
            String stringForm = val.toString();
            String quoted = StringQuote.qqJavaString(stringForm);
            return String.format("new %s(%s)", ctx.name(type), quoted);
        };
    }

    public static boolean isPublicStaticParsePresent(Class<?> type) {
        Method parseMethod = null;
        try {
            parseMethod = type.getMethod("parse", String.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
        int modifiers = parseMethod.getModifiers();
        if (!Modifier.isPublic(modifiers))
            return false;
        if (!Modifier.isStatic(modifiers))
            return false;
        return true;
    }

    public static JavaConstructor<Object> staticParseCtor(Class<?> type) {
        return staticParseCtor(type, false);
    }

    public static JavaConstructor<Object> staticParseCtor(Class<?> type, boolean raiseError) {
        Method parseMethod = null;
        try {
            parseMethod = type.getMethod("parse", String.class);
        } catch (NoSuchMethodException e) {
            if (raiseError)
                throw new IllegalArgumentException("class doesn't have a parse method: " + type, e);
            else
                return null;
        }

        int modifiers = parseMethod.getModifiers();
        if (!Modifier.isPublic(modifiers))
            if (raiseError)
                throw new IllegalArgumentException("parse method isn't public: " + parseMethod);
            else
                return null;
        if (!Modifier.isStatic(modifiers))
            if (raiseError)
                throw new IllegalArgumentException("parse method isn't static: " + parseMethod);
            else
                return null;

        return (val, ctx) -> {
            if (val == null)
                return "null";
            String stringForm = val.toString();
            String quoted = StringQuote.qqJavaString(stringForm);
            return String.format("%s.parse(%s)", ctx.name(type), quoted);
        };
    }

}
