package net.bodz.lily.tool.daogen.util;

import java.util.LinkedHashMap;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.esm.util.TsCodeStyle;
import net.bodz.bas.io.ITreeOut;

public class Attrs
        extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static final Object NO_VALUE = new Object();

    public TsCodeStyle codeStyle;

    public Attrs() {
        this(TsCodeStyle.DEFAULT);
    }

    public Attrs(TsCodeStyle codeStyle) {
        this.codeStyle = codeStyle;
    }

    public void putQuoted(String key, Object value) {
        if (value == null)
            return;
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
            boolean newLine = alwaysNewLine || codeStyle.isNewLineProp(key);
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

    String _toMarkup(boolean xml, String tagName, boolean end) {
        StringBuilder sb = new StringBuilder();
        if (tagName != null) {
            sb.append("<");
            sb.append(tagName);
        }

        int i = 0;
        for (String key : keySet()) {
            if (i++ != 0 || tagName != null)
                sb.append(" ");
            Object val = get(key);
            sb.append(key);
            if (xml) {
                sb.append("=");
                if (val == NO_VALUE)
                    sb.append(StringQuote.qqXmlAttr(key));
                else
                    sb.append(StringQuote.qqXmlAttr(val.toString()));
            } else {
                if (val != NO_VALUE) {
                    sb.append("=");
                    sb.append(StringQuote.qqXmlAttr(val.toString()));
                }
            }
        }

        if (tagName != null)
            if (end)
                sb.append(" />");
            else
                sb.append(">");
        return sb.toString();
    }

    public String toXml(String tagName) {
        return toXml(tagName, false);
    }

    public String toXml(String tagName, boolean end) {
        return _toMarkup(true, tagName, end);
    }

    public String toHtml(String tagName) {
        return toHtml(tagName, false);
    }

    public String toHtml(String tagName, boolean end) {
        return _toMarkup(false, tagName, end);
    }

}
