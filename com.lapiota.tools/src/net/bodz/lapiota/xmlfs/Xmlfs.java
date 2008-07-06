package net.bodz.lapiota.xmlfs;

import net.bodz.bas.types.util.PatternProcessor;

import org.dom4j.Namespace;

public class Xmlfs {

    public static String    NS_URI = "net.bodz.lapiota.xmlfs";

    public static Namespace NS     = Namespace.get("", NS_URI);

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
                        buffer.append(escapeName(c));
                    else
                        buffer.append(c);
                }
            }

            @Override
            protected void unmatched(int start, int end) {
                while (start < end) {
                    char c = source.charAt(start++);
                    buffer.append(escapeName(c));
                }
            }
        }.process(fName);
    }

}
