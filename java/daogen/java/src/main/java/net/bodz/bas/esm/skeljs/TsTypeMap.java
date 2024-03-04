package net.bodz.bas.esm.skeljs;

import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.c.type.TypePoMap;
import net.bodz.bas.esm.EsmName;

public class TsTypeMap {

    Map<String, EsmName> map = new HashMap<>();
    TypePoMap<EsmName> abstractMap = new TypePoMap<>();

    protected void addType(Class<?> clazz, EsmName esmName) {
        map.put(clazz.getName(), esmName);
    }

    protected void addAbstractType(Class<?> clazz, EsmName esmName) {
        abstractMap.put(clazz, esmName);
    }

    public EsmName forClass(Class<?> clazz) {
        EsmName esmName = map.get(clazz.getName());
        if (esmName != null)
            return esmName;
        return abstractMap.meet(clazz);
    }

}
