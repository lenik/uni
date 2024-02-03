package net.bodz.lily.tool.daogen.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.io.ITreeOut;

public class Attrs
        extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public Set<String> newLineKeys = new HashSet<>();

    public Attrs() {
        newLineKeys = new HashSet<>();
    }

    public Attrs(Set<String> newLineKeys) {
        this.newLineKeys = newLineKeys;
    }

    public void putQuoted(String key, Object value) {
        String s = value.toString();
        String quoted = StringQuote.qqJavaString(s);
        put(key, quoted);
    }

    public void toJson(ITreeOut out, boolean embraced) {
        toJson(out, embraced, false);
    }

    public void toJson(ITreeOut out, boolean embraced, boolean alwaysNewLine) {
        if (embraced) {
            out.print("{ ");
        }

        int i = 0;
        boolean lastNewLine = false;
        for (String key : keySet()) {
            boolean newLine = alwaysNewLine || newLineKeys.contains(key);
            Object val = get(key);
            if (i++ != 0)
                out.print(", ");
            if (newLine || lastNewLine) {
                out.println();
                out.print("    ");
            }

            out.print(key);
            out.print(": ");
            out.print(val.toString());

            lastNewLine = newLine;
        }

        if (embraced)
            out.print(" }");
    }

    public String toXml(String tagName) {
        return toXml(tagName, false);
    }

    public String toXml(String tagName, boolean end) {
        StringBuilder sb = new StringBuilder();
        if (tagName != null) {
            sb.append("<");
            sb.append(tagName);
        }

        int i = 0;
        for (String key : keySet()) {
            Object val = get(key);
            if (i++ != 0 || tagName != null)
                sb.append(" ");
            sb.append(key);
            sb.append("=");
            sb.append(StringQuote.qqXmlAttr(val.toString()));
        }

        if (tagName != null)
            if (end)
                sb.append(" />");
            else
                sb.append(">");
        return sb.toString();
    }

}
