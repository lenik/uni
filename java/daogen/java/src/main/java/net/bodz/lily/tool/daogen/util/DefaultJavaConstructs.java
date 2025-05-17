package net.bodz.lily.tool.daogen.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import net.bodz.bas.c.java.time.DateTimes;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.codegen.JavaImports;
import net.bodz.bas.t.predef.Predef;

public class DefaultJavaConstructs
        extends AbstractJavaConstructs {

    {
        defineEx(byte.class, (val, ctx) -> ctx.inArray() ? val.toString() : ("(byte)" + val));
        defineEx(Byte.class, (val, ctx) -> ctx.inArray() ? val.toString() : ("(byte)" + val));

        defineEx(short.class, (val, ctx) -> ctx.inArray() ? val.toString() : ("(short)" + val));
        defineEx(Short.class, (val, ctx) -> ctx.inArray() ? val.toString() : ("(short)" + val));

        define(int.class, Object::toString);
        define(Integer.class, Object::toString);

        defineEx(long.class, (val, ctx) -> val + "L");
        defineEx(Long.class, (val, ctx) -> val + "L");

        defineEx(float.class, (val, ctx) -> ctx.inArray() ? val.toString() : val + "f");
        defineEx(Float.class, (val, ctx) -> ctx.inArray() ? val.toString() : val + "f");

        define(double.class, Object::toString);
        define(Double.class, Object::toString);

        define(boolean.class, Object::toString);
        define(Boolean.class, Object::toString);

        define(char.class, StringQuote::qJavaChar);
        define(Character.class, StringQuote::qJavaChar);

        define(String.class, StringQuote::qqJavaString);

        defineImported(BigInteger.class, (val, ctx) -> //
                String.format("new %s(\"%s\")", ctx.name(BigInteger.class), val));

        defineImported(BigDecimal.class, (val, ctx) -> //
                String.format("new %s(\"%s\")", ctx.name(BigDecimal.class), val));

        defineImported(java.util.Date.class, (val, ctx) -> //
                String.format("%s.ISO8601.parse(%s)", //
                        ctx.name(DateTimes.class), //
                        StringQuote.qqJavaString(DateTimes.ISO8601.format(DateTimes._convert(val)))));

        defineImported(java.sql.Date.class, (val, ctx) -> //
                String.format("new %s(%s.ISO_LOCAL_DATE.parse(%s).getTime())", //
                        ctx.name(java.sql.Date.class),//
                        ctx.name(DateTimes.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_LOCAL_DATE.format(DateTimes._convert(val)))));

        defineImported(java.sql.Time.class, (val, ctx) -> //
                String.format("new %s(%s.ISO_LOCAL_TIME.parse(%s).getTime())", //
                        ctx.name(java.sql.Time.class), //
                        ctx.name(DateTimes.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_LOCAL_TIME.format(DateTimes._convert(val)))));

        defineImported(java.sql.Timestamp.class, (val, ctx) -> //
                String.format("new %s(%s.ISO_LOCAL_DATE_TIME.parse(%s).getTime())", //
                        ctx.name(java.sql.Timestamp.class), //
                        ctx.name(DateTimes.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_LOCAL_DATE_TIME.format(DateTimes._convert(val)))));

        defineImported(ZonedDateTime.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_ZONED_DATE_TIME)", //
                        ctx.name(ZonedDateTime.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_ZONED_DATE_TIME.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(OffsetDateTime.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_OFFSET_DATE_TIME)", //
                        ctx.name(OffsetDateTime.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_OFFSET_DATE_TIME.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(OffsetTime.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_OFFSET_TIME)", //
                        ctx.name(OffsetTime.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_OFFSET_TIME.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(LocalDateTime.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_LOCAL_DATE_TIME)", //
                        ctx.name(LocalDateTime.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_LOCAL_DATE_TIME.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(LocalDate.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_LOCAL_DATE)", //
                        ctx.name(LocalDate.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_LOCAL_DATE.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(LocalTime.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_LOCAL_TIME)", //
                        ctx.name(LocalTime.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_LOCAL_TIME.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(Instant.class, (val, ctx) -> //
                String.format("%s.parse(%s, %s.ISO_INSTANT)", //
                        ctx.name(Instant.class), //
                        StringQuote.qqJavaString(DateTimes.ISO_INSTANT.format(val)), //
                        ctx.name(DateTimes.class)));

        defineImported(Predef.class, (val, ctx) -> //
                String.format("%s.%s", ctx.name(val.getClass()), val.getFieldName()));

        defineImported(UUID.class, (val, ctx) -> //
                String.format("%s.fromString(%s)", ctx.name(UUID.class), StringQuote.qqJavaString(val.toString())));

    }

    public static DefaultJavaConstructs INSTANCE = new DefaultJavaConstructs();

    public static void main(String[] args) {
        IJavaConstructs jc = INSTANCE;
        byte[] bytea = "Hello".getBytes();
        JavaImports im = new JavaImports("foo.bar");
        JavaConstructContext ctx = new JavaConstructContext(im);
        String construct = jc.construct(bytea, ctx);
        System.out.println(construct);
    }

}
