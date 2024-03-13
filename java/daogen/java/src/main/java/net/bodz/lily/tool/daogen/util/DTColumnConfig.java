package net.bodz.lily.tool.daogen.util;

import java.util.LinkedHashSet;
import java.util.Set;

import net.bodz.bas.c.string.StringArray;
import net.bodz.lily.entity.esm.DTColumn;

public class DTColumnConfig {

    public boolean hidden;
    public String className;
    public String dataType;
    public String dataFormat;
    public String dataRender;

    public DTColumnConfig parse(DTColumn annotation) {
        if (annotation == null)
            return this;
        this.hidden = annotation.hidden();
        this.className = removeEmpty(annotation.className());
        this.dataType = removeEmpty(annotation.dataFormat());
        this.dataFormat = removeEmpty(annotation.dataFormat());
        this.dataRender = removeEmpty(annotation.dataRender());
        return this;
    }

    static String removeEmpty(String s) {
        if (s == null)
            return null;
        String trimmed = s.trim();
        if (trimmed.isEmpty())
            return null;
        return trimmed;
    }

    public Set<String> classList() {
        Set<String> classList = new LinkedHashSet<>();
        if (className != null)
            for (String token : StringArray.tokens(className))
                if (! token.isEmpty())
                    classList.add(token);
        if (hidden)
            classList.add("hidden");
        return classList;
    }

}
