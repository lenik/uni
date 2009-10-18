package com.lapiota.eclipse.jdt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.lang.Caller;

public class JavaUtil {

    public static Class<?> resolveType(String name, boolean guessInner) {
        ClassLoader loader = Caller.getCallerClassLoader(0);
        while (true) {
            try {
                return Class.forName(name, false, loader);
            } catch (ClassNotFoundException e) {
            }
            if (!guessInner)
                return null;
            int dot = name.lastIndexOf('.');
            if (dot == -1)
                return null;
            name = name.substring(0, dot) + "$" + name.substring(dot + 1);
        }
    }

    public static Object resolveImport(String name) {
        Class<?> clazz = resolveType(name, true);
        if (clazz != null)
            return clazz;
        int dot = name.lastIndexOf('.');
        if (dot == -1)
            return null;
        String member = name.substring(dot + 1);
        name = name.substring(0, dot);
        clazz = resolveType(name, true);
        if (clazz == null)
            return null;
        try {
            return clazz.getField(member);
        } catch (NoSuchFieldException e) {
        }
        List<Method> byname = new ArrayList<Method>();
        for (Method method : clazz.getMethods())
            if (method.getName().equals(member))
                byname.add(method);
        if (!byname.isEmpty())
            return byname;
        return null;
    }

}
