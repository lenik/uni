package net.bodz.bas.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.c.java.util.regex.IPartProcessor;
import net.bodz.bas.c.java.util.regex.TextPrepByParts;

public class ExceptionFormat {

    static final Pattern packagePattern;
    static {
        packagePattern = Pattern.compile("(net\\.bodz\\.(\\w+)\\.\\S+)", Pattern.MULTILINE);
    }

    public static String highlight(Throwable e) {
        StringWriter buffer = new StringWriter(10000);
        e.printStackTrace(new PrintWriter(buffer));
        String stackTrace = buffer.toString();

        String html = stackTrace.replace("<", "&lt;");
        String highlighted = highlightPackageNames(html);
        return highlighted;
    }

    public static String highlightPackageNames(String s) {
        TextPrepByParts prep = TextPrepByParts.match(packagePattern, new IPartProcessor() {

            @Override
            public String process(String part, Matcher matcher) {
                String sub = matcher.group(2);

                String tag = "b";
                String color = null;

                // if (part.contains("$$")) {
                // tag = "i";
                // color = "gray";
                // }

                if ("bas".equals(sub))
                    color = "blue";

                else if ("uni".equals(sub))
                    color = "green";

                else if ("swt".equals(sub))
                    color = "pink";

                String attrs = "";
                if (color != null)
                    attrs = "style='color: " + color + "'";

                String prefix = "<" + tag + " " + attrs + ">";
                String suffix = "</" + tag + ">";

                part = prefix + part + suffix;
                return part;
            }
        });

        s = prep.process(s);
        return s;
    }

}
