package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.c.type.ClassNames;
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

    public String resolveType(Type genericType, String propertyName) {
        return resolve(true, genericType, propertyName);
    }

    public String resolveType(QualifiedName qName, String propertyName) {
        return resolve(true, qName, propertyName);
    }

    public String resolveType(String javaType, String propertyName) {
        return resolve(true, javaType, propertyName);
    }

    public String resolveType(Class<?> clazz, String propertyName) {
        return resolve(true, clazz, propertyName);
    }

    public String resolveValue(Type genericType, String propertyName) {
        return resolve(false, genericType, propertyName);
    }

    public String resolveValue(QualifiedName qName, String propertyName) {
        return resolve(false, qName, propertyName);
    }

    public String resolveValue(String javaType, String propertyName) {
        return resolve(false, javaType, propertyName);
    }

    public String resolveValue(Class<?> clazz, String propertyName) {
        return resolve(false, clazz, propertyName);
    }

    public String resolve(boolean typeName, Type genericType, String propertyName) {
        if (genericType instanceof Class<?>)
            return resolve(typeName, (Class<?>) genericType, propertyName);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class<?> rawType = (Class<?>) pt.getRawType();
            Class<?>[] bounds = TypeParam.genericBaseBounds(genericType);

            if (List.class.isAssignableFrom(rawType))
                return resolve(typeName, bounds[0], propertyName) + "[]";

            if (Set.class.isAssignableFrom(rawType))
                return resolve(typeName, bounds[0], propertyName) + "[]";

            if (Map.class.isAssignableFrom(rawType))
                return "any";

            return resolve(typeName, rawType, propertyName);
        }
        throw new UnsupportedOperationException();
    }

    public String resolve(boolean typeName, QualifiedName qName, String propertyName) {
        return resolve(typeName, qName.getFullName(), propertyName);
    }

    public String resolve(boolean typeName, String javaType, String propertyName) {
        Class<?> clazz;
        try {
            clazz = ClassNames.resolve(javaType);
            return resolve(typeName, clazz, propertyName);
        } catch (ClassNotFoundException e) {
            if (typeName)
                return naming.importDefaultType(javaType);
            else
                return naming.importDefault(javaType);
        }
    }

    public String resolve(boolean typeName, Class<?> clazz, String propertyName) {
        if (clazz == Object.class)
            throw new Error("Object.class: " + propertyName);

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

        if (clazz.isArray())
            return resolve(typeName, clazz.getComponentType(), propertyName + "[]") + "[]";

        if (clazz == InetAddress.class)
            return "string";

        if (clazz == State.class)
            return "string";

        if (clazz == JsonVariant.class)
            return "any";

        if (Predef.class.isAssignableFrom(clazz)) {
            PredefMetadata<?, ?> metadata = PredefMetadata._forClass(clazz);
            Class<?> keyType = metadata.getKeyType();
            return resolve(typeName, keyType, propertyName);
        }

        if (JsonMap.class.isAssignableFrom(clazz))
            return "any";

        if (typeName)
            return naming.importDefaultType(clazz);
        else
            return naming.importDefault(clazz);
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
