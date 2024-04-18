package net.bodz.lily.tool.daogen.dir.dao.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import net.bodz.bas.c.java.time.DateTimes;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.codegen.IJavaImporter;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.t.predef.Predef;

public class JavaSamples
        extends RandomBased {

    int maxStringLen = 1000;
    IJavaImporter naming;

    public JavaSamples(long rootSeed, Object seedObj, IJavaImporter naming) {
        super(rootSeed, seedObj);
        this.naming = naming;
    }

    public String string(int maxLen) {
        if (maxLen <= 0)
            throw new IllegalArgumentException("invalid varchar length: " + maxLen);
        if (maxLen > maxStringLen)
            maxLen = maxStringLen;
        int wordMaxLen = 10;
        String sample = enGen.makeText(maxLen, wordMaxLen);
        return StringQuote.qqJavaString(sample.toString());
    }

    public String bigDecimal(int precision, int scale) {
        int intLen = precision;
        if (scale != 0)
            intLen -= scale; // + 1 (dot);

        StringBuilder sb = new StringBuilder(precision);
        randomDigits(sb, random.nextInt(intLen + 1), true, random);

        if (scale != 0 && random.nextBoolean()) {
            sb.append('.');
            randomDigits(sb, scale, false, random);
        }

        naming.importName(BigDecimal.class);
        return "new BigDecimal(\"" + sb + "\")";
    }

    public String bigInteger(int maxLen) {
        int len = random.nextInt(maxLen) + 1;
        StringBuilder sb = new StringBuilder(len);
        randomDigits(sb, random.nextInt(len + 1), true, random);
        naming.importName(BigInteger.class);
        return "new BigInteger(\"" + sb + "\")";
    }

    public String date(Date value, Class<?> type) {
        DateTimeFormatter formatter;
        String formatName;
        switch (TypeKind.getTypeId(type)) {
        case TypeId.SQL_DATE:
            formatter = DateTimes.ISO_LOCAL_DATE;
            formatName = "ISO_LOCAL_DATE";
            break;
        case TypeId.SQL_TIME:
            formatter = DateTimes.ISO_LOCAL_TIME;
            formatName = "ISO_LOCAL_TIME";
            break;
        case TypeId.TIMESTAMP:
            formatter = DateTimes.ISO_LOCAL_DATE_TIME;
            formatName = "ISO_LOCAL_DATE_TIME";
            break;
        case TypeId.DATE:
        default:
            formatter = DateTimes.ISO8601;
            formatName = "ISO8601";
        }
        TemporalAccessor temporal = DateTimes._convert(value);
        String literal;
        try {
            literal = formatter.format(temporal);
        } catch (DateTimeException e) {
            throw new IllegalUsageException(String.format(//
                    "error format %s \"%s\" in %s.", //
                    temporal.getClass().getSimpleName(), temporal, //
                    formatName));
        }
        String literalQuoted = StringQuote.qqJavaString(literal);

        String dateExpr = String.format("%s.%s.parse(%s)", //
                naming.importName(DateTimes.class), //
                formatName, //
                literalQuoted);
        String timeExpr = dateExpr + ".getTime()";

        if (type == null)
            type = Date.class;

        switch (TypeKind.getTypeId(type)) {
        case TypeId.DATE:
            return dateExpr;

        case TypeId.SQL_DATE:
            return String.format("new %s(%s)", naming.importName(java.sql.Date.class), timeExpr);

        case TypeId.SQL_TIME:
            return String.format("new %s(%s)", naming.importName(java.sql.Time.class), timeExpr);

        case TypeId.TIMESTAMP:
            return String.format("new %s(%s)", naming.importName(java.sql.Timestamp.class), timeExpr);

        default:
            return String.format("new %s(%s)", naming.importName(type), timeExpr);
        }
    }

    public String javaTime(TemporalAccessor temporal, Class<?> type) {
        DateTimeFormatter formatter;
        String formatName;
        switch (TypeKind.getTypeId(type)) {
        case TypeId.INSTANT:
            formatter = DateTimes.ISO_INSTANT;
            formatName = "ISO_INSTANT";
            break;
        case TypeId.LOCAL_DATE_TIME:
            formatter = DateTimes.ISO_LOCAL_DATE_TIME;
            formatName = "ISO_LOCAL_DATE_TIME";
            break;
        case TypeId.LOCAL_DATE:
            formatter = DateTimes.ISO_LOCAL_DATE;
            formatName = "ISO_LOCAL_DATE";
            break;
        case TypeId.LOCAL_TIME:
            formatter = DateTimes.ISO_LOCAL_TIME;
            formatName = "ISO_LOCAL_TIME";
            break;
        case TypeId.OFFSET_DATE_TIME:
            formatter = DateTimes.ISO_OFFSET_DATE_TIME;
            formatName = "ISO_OFFSET_DATE_TIME";
            break;
        case TypeId.OFFSET_TIME:
            formatter = DateTimes.ISO_OFFSET_TIME;
            formatName = "ISO_OFFSET_TIME";
            break;
        case TypeId.ZONED_DATE_TIME:
            formatter = DateTimes.ISO_ZONED_DATE_TIME;
            formatName = "ISO_ZONED_DATE_TIME";
            break;
        default:
            throw new UnexpectedException();
        }

        String literal = formatter.format(temporal);
        String literalQuoted = StringQuote.qqJavaString(literal);

        switch (TypeKind.getTypeId(type)) {
        case TypeId.INSTANT:
        case TypeId.LOCAL_DATE_TIME:
        case TypeId.LOCAL_DATE:
        case TypeId.LOCAL_TIME:
        case TypeId.OFFSET_DATE_TIME:
        case TypeId.OFFSET_TIME:
        case TypeId.ZONED_DATE_TIME:
            String parseExpr = String.format("%s.parse(%s, %s.%s)", //
                    naming.importName(type), //
                    literalQuoted, //
                    naming.importName(DateTimes.class), formatName);
            return parseExpr;

        default:
            throw new UnsupportedOperationException(type.getName());
        }
    }

    public String predef(Predef<?, ?> value, Class<?> type) {
        String name = value.getName();
        String fieldName = value.getFieldName();
        return String.format("%s.%s", naming.importName(type), fieldName);
    }

    /**
     * @return <code>null</code> if unsupported type.
     */
    public String simpleValue(Object value, Class<?> type) {
        String str = value.toString();
        switch (TypeKind.getTypeId(type)) {
        case TypeId._byte:
        case TypeId.BYTE:
            return "(byte)" + str;

        case TypeId._short:
        case TypeId.SHORT:
            return "(short)" + str;

        case TypeId._int:
        case TypeId.INTEGER:
            return str;

        case TypeId._long:
        case TypeId.LONG:
            return str + "L";

        case TypeId._float:
        case TypeId.FLOAT:
            return str + "f";

        case TypeId._double:
        case TypeId.DOUBLE:
            return str;

        case TypeId._boolean:
        case TypeId.BOOLEAN:
            return str;

        default:
            return null;
        }
    }

    /**
     * @return <code>null</code> if unsupported type.
     */
    public String parseString(String str, Class<?> type) {
        try {
            type.getMethod("parse", String.class);
            String simpleName = naming.importName(type);
            return String.format("%s.parse(%s)", simpleName, StringQuote.qqJavaString(str));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
