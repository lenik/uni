package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.c.string.StringQuote;

public class JavaLiterals
        implements
            ILiteralCodeFormatter {

    public static final String NULL = "null";

    @Override
    public String compile(Object obj) {
        if (obj == null)
            return NULL;
        else
            return compile(obj.toString());
    }

    @Override
    public String compile(String str) {
        if (str == null)
            return NULL;
        else
            return StringQuote.qqJavaString(str);
    }

    @Override
    public String compile(String format, Object... args) {
        String s = String.format(format, args);
        return StringQuote.qqJavaString(s);
    }

    @Override
    public String compile(StringBuffer sb) {
        if (sb == null)
            return NULL;
        return compile(sb.toString());
    }

    @Override
    public String compile(CharSequence s) {
        if (s == null)
            return NULL;
        return compile(s.toString());
    }

    @Override
    public String compile(CharSequence s, int start, int end) {
        String str = s.subSequence(start, end).toString();
        return compile(str);
    }

    @Override
    public String compile(char[] str) {
        String s = new String(str);
        return compile(s);
    }

    @Override
    public String compile(char[] str, int offset, int len) {
        String s = new String(str, offset, len);
        return compile(s);
    }

    @Override
    public String compile(boolean b) {
        return b ? "true" : "false";
    }

    @Override
    public String compile(char c) {
        String singleChar = String.valueOf(c);
        return StringQuote.qJavaString(singleChar);
    }

    @Override
    public String compile(int i) {
        return String.valueOf(i);
    }

    @Override
    public String compile(long lng) {
        return String.valueOf(lng);
    }

    @Override
    public String compile(float f) {
        return String.valueOf(f);
    }

    @Override
    public String compile(double d) {
        return String.valueOf(d);
    }

    public static final JavaLiterals INSTANCE = new JavaLiterals();

}
