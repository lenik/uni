package net.bodz.uni.jnigen;

import java.util.LinkedHashMap;

public class ClassMap
        extends LinkedHashMap<Class<?>, Object> {

    private static final long serialVersionUID = 1L;

    public boolean add(Class<?> clazz) {
        Object old = put(clazz, true);
        return old == null;
    }

    public boolean containsSuperclass(Class<?> clazz) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null)
            return false;
        return containsKey(superclass);
    }

}
