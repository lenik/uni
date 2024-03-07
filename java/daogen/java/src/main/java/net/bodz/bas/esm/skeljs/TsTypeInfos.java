package net.bodz.bas.esm.skeljs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.skeljs.Core.BasInfo;
import net.bodz.bas.esm.skeljs.Core.BaseInfo;
import net.bodz.bas.fmt.json.JsonVariant;

public class TsTypeInfos
        extends TsTypeMap {

    {
        BaseInfo baseInfo = EsmModules.core.baseInfo;
        BasInfo basInfo = EsmModules.core.basInfo;

        addType(byte.class, baseInfo.BYTE);
        addType(short.class, baseInfo.SHORT);
        addType(int.class, baseInfo.INT);
        addType(long.class, baseInfo.LONG);
        addType(float.class, baseInfo.FLOAT);
        addType(double.class, baseInfo.DOUBLE);
        addType(boolean.class, baseInfo.BOOLEAN);
        addType(char.class, baseInfo.CHAR);

        addType(Byte.class, baseInfo.BYTE);
        addType(Short.class, baseInfo.SHORT);
        addType(Integer.class, baseInfo.INT);
        addType(Long.class, baseInfo.LONG);
        addType(Float.class, baseInfo.FLOAT);
        addType(Double.class, baseInfo.DOUBLE);
        addType(Boolean.class, baseInfo.BOOLEAN);
        addType(Character.class, baseInfo.CHAR);
        addType(String.class, baseInfo.STRING);

        addType(BigInteger.class, baseInfo.BIG_INTEGER);
        addType(BigDecimal.class, baseInfo.BIG_DECIMAL);

        addType(Date.class, baseInfo.DATE);
        addType(java.sql.Date.class, baseInfo.DATE);
        addType(Timestamp.class, baseInfo.DATE);

        addAbstractType(List.class, baseInfo.LIST);
        addAbstractType(Set.class, baseInfo.SET);
        addAbstractType(Map.class, baseInfo.MAP);

        addType(JsonVariant.class, basInfo.JSON_VARIANT);
        addAbstractType(InetAddress.class, EsmModules.core.baseInfo.INET_ADDRESS);
    }

    @Override
    public EsmName forClass(Class<?> clazz) {
        EsmName esmName = super.forClass(clazz);
        if (esmName != null)
            return esmName;

        if (clazz.isArray())
            return EsmModules.core.baseInfo.ARRAY;
        if (clazz.isEnum())
            return EsmModules.core.baseInfo.ENUM;

        return null;
    }

    public static final TsTypeInfos INSTANCE = new TsTypeInfos();

}
