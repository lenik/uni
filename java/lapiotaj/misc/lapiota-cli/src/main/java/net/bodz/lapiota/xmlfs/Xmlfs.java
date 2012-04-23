package net.bodz.lapiota.xmlfs;

import net.bodz.bas.c.java.util.regex.PatternProcessor;

import org.dom4j.Namespace;

public class Xmlfs {

    public static String NS_URI = "net.bodz.uni.xmlfs"; //$NON-NLS-1$

    public static Namespace NS = Namespace.get("", NS_URI); //$NON-NLS-1$

    static String escapeName(char c) {
        if (c == '_')
            return "__"; //$NON-NLS-1$
        if (c == '.')
            return "."; //$NON-NLS-1$
        if (Character.isLetterOrDigit(c))
            return String.valueOf(c);
        return String.format("_%04x", (int) c); //$NON-NLS-1$
    }

    public static String getXName(String fName) {
        return new PatternProcessor("\\w|[.]") { //$NON-NLS-1$
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
