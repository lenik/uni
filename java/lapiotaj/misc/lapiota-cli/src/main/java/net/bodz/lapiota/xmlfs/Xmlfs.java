package net.bodz.lapiota.xmlfs;

import org.dom4j.Namespace;

import net.bodz.bas.c.java.util.regex.PatternProcessor;

public class Xmlfs {

    public static String NS_URI = "net.bodz.uni.xmlfs";

    public static Namespace NS = Namespace.get("", NS_URI);

    static String escapeName(char c) {
        if (c == '_')
            return "__";
        if (c == '.')
            return ".";
        if (Character.isLetterOrDigit(c))
            return String.valueOf(c);
        return String.format("_%04x", (int) c);
    }

    public static String getXName(String fName) {
        return new PatternProcessor("\\w|[.]") {
            @Override
            protected void matched(int start, int end) {
                while (start < end) {
                    char c = source.charAt(start++);
                    if (c == '_')
                        append(escapeName(c));
                    else
                        append(c);
                }
            }

            @Override
            protected void unmatched(int start, int end) {
                while (start < end) {
                    char c = source.charAt(start++);
                    append(escapeName(c));
                }
            }
        }.process(fName);
    }

}
