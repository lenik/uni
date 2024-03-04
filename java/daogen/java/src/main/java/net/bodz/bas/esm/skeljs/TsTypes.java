package net.bodz.bas.esm.skeljs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Date;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.skeljs.Core.BasType;
import net.bodz.bas.esm.skeljs.Core.BaseType;
import net.bodz.bas.esm.skeljs.Core.LangTime;
import net.bodz.bas.fmt.json.JsonVariant;
import net.bodz.bas.repr.state.State;
import net.bodz.bas.site.json.JsonMap;

public class TsTypes
        extends TsTypeMap {

    {
        BaseType baseType = EsmModules.core.baseType;
        BasType basType = EsmModules.core.basType;
        LangTime time = EsmModules.core.time;

        addType(byte.class, baseType._byte);
        addType(short.class, baseType._short);
        addType(int.class, baseType._int);
        addType(long.class, baseType._long);
        addType(float.class, baseType._float);
        addType(double.class, baseType._double);
        addType(boolean.class, baseType._boolean);
        addType(char.class, baseType._char);

        addType(Byte.class, baseType._byte);
        addType(Short.class, baseType._short);
        addType(Integer.class, baseType._int);
        addType(Long.class, baseType._long);
        addType(Float.class, baseType._float);
        addType(Double.class, baseType._double);
        addType(Boolean.class, baseType._boolean);
        addType(Character.class, baseType._char);
        addType(String.class, baseType._string);

        addType(BigInteger.class, baseType.BigInteger);
        addType(BigDecimal.class, baseType.BigDecimal);

        addType(Date.class, baseType._Date);
        addType(java.sql.Date.class, baseType.SQLDate);
        addType(Timestamp.class, baseType.Timestamp);

//        addAbstractType(List.class, baseType._List);
//        addAbstractType(Set.class, baseType._Set);
//        addAbstractType(Map.class, baseType._Map);

        addAbstractType(InetAddress.class, baseType.InetAddress);

        addType(JsonVariant.class, basType.JsonVariant);
        addType(JsonMap.class, basType.JsonVariant);
        addType(State.class, baseType._int);

        addType(Instant.class, time.Instant);
        addType(LocalDate.class, time.LocalDate);
        addType(LocalTime.class, time.LocalTime);
        addType(LocalDateTime.class, time.LocalDateTime);
        addType(OffsetTime.class, time.OffsetTime);
        addType(OffsetDateTime.class, time.OffsetDateTime);
        addType(ZonedDateTime.class, time.ZonedDateTime);

    }

    public EsmName forClassName(String className) {
        return map.get(className);
    }

    @Override
    public EsmName forClass(Class<?> clazz) {
        EsmName esmName = super.forClass(clazz);
        if (esmName != null)
            return esmName;

        if (clazz.isEnum())
            return EsmModules.core.baseType.Enum;

        return null;
    }

    public static final TsTypes INSTANCE = new TsTypes();

}
