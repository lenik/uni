package net.bodz.uni.jnigen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TypeNames {

    public static boolean packageContains(String parent, String child) {
        if (parent == null)
            return false;
        if (child.startsWith(parent)) {
            int parlen = parent.length();
            if (child.length() == parlen)
                return true;
            else if (child.charAt(parlen) == '.')
                return true;
        }
        return false;
    }

    public static String getName(Class<?> clazz, boolean withNs) {
        if (withNs)
            return getNameWithNS(clazz);
        else
            return clazz.getSimpleName();
    }

    public static String getNameWithNS(Class<?> clazz) {
        String ns = clazz.getPackage().getName().replace(".", "::");
        String name = clazz.getSimpleName();
        if (ns.isEmpty())
            return name;
        else
            return ns + "::" + name;
    }

    public static String getCtorVarName(String ctorName, Constructor<?> ctor) {
        return "INIT" + ctorName;
    }

    public static String getMethodVarName(String methodName, Method method) {
        return "METHOD_" + methodName;
    }

}
