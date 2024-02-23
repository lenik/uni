package net.bodz.lily.tool.daogen.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.bodz.bas.c.type.TypePoMap;
import net.bodz.bas.t.predef.Predef;

public class CriteriaBuilderFieldInfo {

    public final Class<?> type;
    public final String fieldType;
    public final String creatorFn;

    public CriteriaBuilderFieldInfo(Class<?> type, String fieldType, String creatorFn) {
        this.type = type;
        this.fieldType = fieldType;
        this.creatorFn = creatorFn;
    }

    static final TypePoMap<CriteriaBuilderFieldInfo> map = new TypePoMap<>();

    public static CriteriaBuilderFieldInfo meet(Class<?> type) {
        return map.meet(type);
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

        // discrete types but in text form.
        declare(Predef.class, "StringField", "string");
    }

}
