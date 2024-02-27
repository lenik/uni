package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.c.primitive.Primitives;
import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.c.type.TypeParam;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.IImportTsNaming;
import net.bodz.bas.fmt.json.JsonVariant;
import net.bodz.bas.repr.state.State;
import net.bodz.bas.site.json.JsonMap;
import net.bodz.bas.t.predef.Predef;
import net.bodz.bas.t.predef.PredefMetadata;
import net.bodz.bas.t.tuple.QualifiedName;

public class TsTypeResolver {

    final IImportTsNaming naming;

    public TsTypeResolver(IImportTsNaming naming) {
        if (naming == null)
            throw new NullPointerException("naming");
        this.naming = naming;
    }

    public String resolve(Type genericType, String property) {
        if (genericType instanceof Class<?>)
            return resolve((Class<?>) genericType, property);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class<?> rawType = (Class<?>) pt.getRawType();
            Class<?>[] bounds = TypeParam.genericBaseBounds(genericType);

            if (List.class.isAssignableFrom(rawType))
                return resolve(bounds[0], property) + "[]";

            if (Set.class.isAssignableFrom(rawType))
                return resolve(bounds[0], property) + "[]";

            if (Map.class.isAssignableFrom(rawType))
                return "any";

            return resolve(rawType, property);
        }
        throw new UnsupportedOperationException();
    }

    public String resolve(QualifiedName javaType, String property) {
        return resolve(javaType.getFullName(), property);
    }

    public String resolve(String javaType, String property) {
        Class<?> clazz;
        try {
            clazz = Primitives.forName(javaType);
        } catch (ClassNotFoundException e) {
            return javaType;
        }
        return resolve(clazz, property);
    }

    public String resolve(Class<?> clazz, String property) {
        if (clazz == Object.class)
            throw new Error("Object.class: " + property);

        int typeId = TypeKind.getTypeId(clazz);
        switch (typeId) {
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

        case TypeId.ENUM:
            return "string";

        case TypeId.STRING:
            return "string";
        }

        if (clazz.isEnum())
            return "string";

        EsmName coreType = coreType(clazz);
        if (coreType != null)
            return naming.importName(coreType);

        if (clazz == InetAddress.class)
            return "string";

        if (clazz == State.class)
            return "string";

        if (clazz == JsonVariant.class)
            return "any";

        if (Predef.class.isAssignableFrom(clazz)) {
            PredefMetadata<?, ?> metadata = PredefMetadata._forClass(clazz);
            Class<?> keyType = metadata.getKeyType();
            return resolve(keyType, property);
        }

        if (JsonMap.class.isAssignableFrom(clazz))
            return "any";

        return naming.importName(clazz);
    }

    static EsmName coreType(Class<?> clazz) {
        int typeId = TypeKind.getTypeId(clazz);
        switch (typeId) {
        case TypeId._char:
        case TypeId.CHARACTER:
            return EsmModules.core.type._char;

        case TypeId._byte:
        case TypeId.BYTE:
            return EsmModules.core.type._byte;

        case TypeId._short:
        case TypeId.SHORT:
            return EsmModules.core.type._short;

        case TypeId._int:
        case TypeId.INTEGER:
            return EsmModules.core.type.integer;

        case TypeId._long:
        case TypeId.LONG:
            return EsmModules.core.type._long;

        case TypeId._float:
        case TypeId.FLOAT:
            return EsmModules.core.type._float;

        case TypeId._double:
        case TypeId.DOUBLE:
            return EsmModules.core.type._double;

        case TypeId.INSTANT:
            return EsmModules.core.time.Instant;
        case TypeId.ZONED_DATE_TIME:
            return EsmModules.core.time.ZonedDateTime;
        case TypeId.OFFSET_DATE_TIME:
            return EsmModules.core.time.OffsetDateTime;
        case TypeId.LOCAL_DATE_TIME:
            return EsmModules.core.time.LocalDateTime;
        case TypeId.LOCAL_DATE:
            return EsmModules.core.time.LocalDate;
        case TypeId.LOCAL_TIME:
            return EsmModules.core.time.LocalTime;
        case TypeId.OFFSET_TIME:
            return EsmModules.core.time.OffsetTime;

        default:
            return null;
        }
    }

}
