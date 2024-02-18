package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.c.primitive.Primitives;
import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.c.type.TypeParam;
import net.bodz.bas.codegen.IImportNaming;
import net.bodz.bas.site.json.JsonMap;
import net.bodz.bas.t.tuple.QualifiedName;

import antlr.collections.List;

public class TsTypeResolver {

    final IImportNaming naming;

    public TsTypeResolver(IImportNaming naming) {
        if (naming == null)
            throw new NullPointerException("naming");
        this.naming = naming;
    }

    public String resolve(Type genericType) {
        if (genericType instanceof Class<?>)
            return resolve((Class<?>) genericType);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class<?> rawType = (Class<?>) pt.getRawType();
            Class<?>[] bounds = TypeParam.genericBaseBounds(genericType);

            if (List.class.isAssignableFrom(rawType))
                return resolve(bounds[0]) + "[]";

            if (Set.class.isAssignableFrom(rawType))
                return resolve(bounds[0]) + "[]";

            if (Map.class.isAssignableFrom(rawType))
                return "any";

            return resolve(rawType);
        }
        throw new UnsupportedOperationException();
    }

    public String resolve(QualifiedName javaType) {
        return resolve(javaType.getFullName());
    }

    public String resolve(String javaType) {
        Class<?> clazz;
        try {
            clazz = Primitives.forName(javaType);
        } catch (ClassNotFoundException e) {
            return javaType;
        }
        return resolve(clazz);
    }

    public String resolve(Class<?> clazz) {
        int typeId = TypeKind.getTypeId(clazz);
        switch (typeId) {
        case TypeId._char:
        case TypeId.CHARACTER:
            return "char";

        case TypeId._byte:
        case TypeId.BYTE:
            return "byte";

        case TypeId._short:
        case TypeId.SHORT:
            return "short";

        case TypeId._int:
        case TypeId.INTEGER:
            return "integer";

        case TypeId._long:
        case TypeId.LONG:
            return "long";

        case TypeId._float:
        case TypeId.FLOAT:
            return "float";

        case TypeId._double:
        case TypeId.DOUBLE:
            return "double";

        case TypeId._boolean:
        case TypeId.BOOLEAN:
            return "boolean";

        case TypeId.BIG_INTEGER:
        case TypeId.BIG_DECIMAL:
            return "BigInteger";

        case TypeId.DATE:
        case TypeId.SQL_DATE:
        case TypeId.TIMESTAMP:
            return "Date";

        case TypeId.INSTANT:
        case TypeId.ZONED_DATE_TIME:
        case TypeId.OFFSET_DATE_TIME:
        case TypeId.LOCAL_DATE_TIME:
        case TypeId.LOCAL_DATE:
        case TypeId.LOCAL_TIME:
        case TypeId.OFFSET_TIME:
            return "Moment";

        case TypeId.STRING:
            return "string";
        }

        if (JsonMap.class.isAssignableFrom(clazz))
            return "any";

        return naming.importName(clazz);
    }

}
