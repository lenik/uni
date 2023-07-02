package net.bodz.lily.tool.daogen.util;

interface ILiteralCodeFormatter {

    String compile(Object obj);

    String compile(String str);

    String compile(String format, Object... args);

    String compile(StringBuffer sb);

    String compile(CharSequence s);

    String compile(CharSequence s, int start, int end);

    String compile(char[] str);

    String compile(char[] str, int offset, int len);

    String compile(boolean b);

    String compile(char c);

    String compile(int i);

    String compile(long lng);

    String compile(float f);

    String compile(double d);

}
