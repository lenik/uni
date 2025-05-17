package net.bodz.lily.tool.daogen.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.c.type.TypePoMap;
import net.bodz.bas.meta.decl.NotNull;

public abstract class AbstractJavaConstructs
        implements IMutableJavaConstructs {

    TypePoMap<JavaConstructor<Object>> typeMap = new TypePoMap<>();
    Map<Integer, JavaConstructor<Object>> sqlTypeMap = new HashMap<>();
    Map<String, JavaConstructor<Object>> sqlTypeNameMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> void define(Class<T> type, JavaSimpleConstructor<T> ctor) {
        typeMap.put(type, (JavaConstructor<Object>) ctor);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> void define(int sqlType, JavaSimpleConstructor<T> ctor) {
        sqlTypeMap.put(sqlType, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void define(String sqlTypeName, JavaSimpleConstructor<T> ctor) {
        sqlTypeNameMap.put(sqlTypeName, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void defineEx(Class<T> type, JavaConstructor<T> ctor) {
        typeMap.put(type, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void defineEx(int sqlType, JavaConstructor<T> ctor) {
        sqlTypeMap.put(sqlType, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void defineEx(String sqlTypeName, JavaConstructor<T> ctor) {
        sqlTypeNameMap.put(sqlTypeName, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void defineImported(Class<T> type, JavaConstructor<T> ctor) {
        typeMap.put(type, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void defineImported(int sqlType, JavaConstructor<T> ctor) {
        sqlTypeMap.put(sqlType, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void defineImported(String sqlTypeName, JavaConstructor<T> ctor) {
        sqlTypeNameMap.put(sqlTypeName, (JavaConstructor<Object>) ctor);
    }

    @SuppressWarnings("unchecked")
    public <T> JavaConstructor<T> getConstructor(@NotNull Class<T> type) {
        if (typeMap.containsKey(type))
            return (JavaConstructor<T>) typeMap.get(type);

        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            JavaConstructor<Object> componentCtor = (JavaConstructor<Object>) getConstructor(componentType);
            if (componentCtor == null)
                return null;
            return (array, ctx) -> {
                JavaConstructContext elContext = new JavaConstructContext(ctx.getImporter());
                elContext.setInArray(true);

                int len = Array.getLength(array);
                StringBuilder buf = new StringBuilder(len * 10);
                buf.append(String.format("new %s[] { ", ctx.name(componentType)));

                for (int i = 0; i < len; i++) {
                    Object el = Array.get(array, i);
                    String elConstruct = componentCtor.construct(el, elContext);
                    if (i > 0)
                        buf.append(", ");
                    buf.append(elConstruct);
                }
                buf.append(" }");
                return buf.toString();
            };
        }

        if (JavaConstructs.isStringCtorPresent(type))
            return (JavaConstructor<T>) JavaConstructs.stringCtor(type);

        if (JavaConstructs.isPublicStaticParsePresent(type))
            return (JavaConstructor<T>) JavaConstructs.staticParseCtor(type);

        return null;
    }

    @Override
    public String construct(Object val, IJavaConstructContext context) {
        if (val == null)
            return "null";

        @SuppressWarnings("unchecked")
        JavaConstructor<Object> ctor = (JavaConstructor<Object>) getConstructor(val.getClass());

        if (ctor != null)
            return ctor.construct(val, context);
        else
            return null;
    }

    @Override
    public String construct(int sqlType, Object val, IJavaConstructContext context) {
        if (val == null)
            return "null";
        JavaConstructor<Object> ctor = sqlTypeMap.get(sqlType);
        if (ctor == null)
            return null;
        return ctor.construct(val, context);
    }

    @Override
    public String construct(String sqlTypeName, Object val, IJavaConstructContext context) {
        if (val == null)
            return "null";
        JavaConstructor<Object> ctor = sqlTypeNameMap.get(sqlTypeName);
        if (ctor == null)
            return null;
        return ctor.construct(val, context);
    }

}
