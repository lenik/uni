package net.bodz.lily.tool.daogen.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CriteriaBuilderFieldInfo {

    public final Class<?> type;
    public final String fieldType;
    public final String creatorFn;

    public CriteriaBuilderFieldInfo(Class<?> type, String fieldType, String creatorFn) {
        this.type = type;
        this.fieldType = fieldType;
        this.creatorFn = creatorFn;
    }

    static final Map<Class<?>, CriteriaBuilderFieldInfo> map = new HashMap<>();

    public static CriteriaBuilderFieldInfo get(Class<?> type) {
        return map.get(type);
    }

    static void declare(Class<?> type, String fieldType, String creatorFn) {
        CriteriaBuilderFieldInfo info = new CriteriaBuilderFieldInfo(type, fieldType, creatorFn);
        map.put(type, info);
    }

    static {
        declare(Byte.class, "ByteField", "_byte");
        declare(Short.class, "ShortField", "_short");
        declare(Integer.class, "IntegerField", "integer");
        declare(Long.class, "LongField", "_long");
        declare(Float.class, "FloatField", "_float");
        declare(Double.class, "DoubleField", "_double");
        declare(BigInteger.class, "BigIntegerField", "bigInteger");
        declare(BigDecimal.class, "BigDecimalField", "bigDecimal");
        declare(Boolean.class, "BooleanField", "bool");
        declare(String.class, "StringField", "string");
    }

}
