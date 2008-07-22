package net.bodz.lapiota.xmlfs;

import net.bodz.bas.text.interp.PatternProcessor;

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
                        print(escapeName(c));
                    else
                        print(c);
                }
            }

            @Override
            protected void unmatched(int start, int end) {
                while (start < end) {
                    char c = source.charAt(start++);
                    print(escapeName(c));
                }
            }
        }.process(fName);
    }

}
