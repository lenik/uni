package net.bodz.lily.tool.javagen.config;

import net.bodz.bas.c.string.StringQuote;

public class SuffixDecorator
        implements
            INameDecorator {

    final String suffix;
    final int suffixLen;

    public SuffixDecorator(String suffix) {
        if (suffix == null)
            throw new NullPointerException("suffix");
        this.suffix = suffix;
        this.suffixLen = suffix.length();
    }

    @Override
    public boolean isDecorated(String s) {
        if (s == null)
            return false;
        return s.endsWith(suffix);
    }

    @Override
    public String decorate(String s) {
        if (s == null)
            return null;
        return s + suffix;
    }

    @Override
    public String undecorate(String s) {
        if (s == null)
            return null;
        if (s.endsWith(suffix))
            return s.substring(0, s.length() - suffixLen);
        else
            return s;
    }

    @Override
    public String toString() {
        return "ends-with(" + StringQuote.qqJavaString(suffix) + ")";
    }

}
