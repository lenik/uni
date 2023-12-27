package net.bodz.bas.fmt.excel.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.typer.Typers;
import net.bodz.bas.typer.std.IParser;

public class ExcelConv
        implements
            IDataParser {

    static final Logger logger = LoggerFactory.getLogger(ExcelConv.class);

    static final DateFormat timestampFmt = new SimpleDateFormat(//
            "yy-MM-dd HH:mm:ss.SSSSSS aa");
    static final DateFormat dateTimeFmt = new SimpleDateFormat(//
            "yy-MM-dd HH:mm:ss");
    static final DateFormat engDateTimeFmt = new SimpleDateFormat(//
            "dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
    static final DateFormat engDateFmt = new SimpleDateFormat(//
            "dd-MMM-yyyy", Locale.ENGLISH);

    public ExcelConv() {
    }

    @Override
    public Object parse(IColumnMetadata column, String str)
            throws ParseException {
        if ("\\N".equals(str))
            return null;
        Class<?> type = column.getJavaClass();
        try {
            switch (column.getJdbcType()) {
            case DATE:
                if (str == null || str.isEmpty())
                    return null;
                else {
                    try {
                        return dateTimeFmt.parse(str);
                    } catch (java.text.ParseException e) {
                        try {
                            return engDateTimeFmt.parse(str);
                        } catch (java.text.ParseException e2) {
                            return engDateFmt.parse(str);
                        }
                    }
                }

            case TIMESTAMP:
                return timestampFmt.parse(str);

            case CHAR:
            case NCHAR:

            case VARCHAR:
            case NVARCHAR:

            case LONGVARCHAR:
            case LONGNVARCHAR:
                int maxLen = column.getPrecision();
                int len = str.length();
                if (len > maxLen)
                    throw new ParseException(String.format("%s too long: %d (max %d)", column.getName(), len, maxLen));
                return str;

            default:
                IParser<?> parser = Typers.getTyper(type, IParser.class);
                return parser.parse(str);
            }
        } catch (Exception e) {
            String mesg = String.format("Error parse column %s with value %s: %s", //
                    column.getName(), str, e.getMessage());
            throw new ParseException(mesg, e);
        }
    }

}
