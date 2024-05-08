package net.bodz.uni.site.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.bodz.bas.rtx.IAttributes;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

/**
 * See: <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 language code</a>.
 */
public enum Language
        implements
            IXjdocAware,
            IAttributes {

    /**
     * English
     */
    ENGLISH("en"),

    /**
     * 简体中文
     */
    SIMPLIFIED_CHINESE("zh-cn"),

    /**
     * 繁体中文
     */
    TRADITIONAL_CHINESE("zh-tw"),

    /**
     * 日本語
     */
    JAPANESE("ja"),

    ;

    private final String code;
    private IElementDoc xjdoc;

    private Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isFullTranslated() {
        switch (this) {
        case ENGLISH:
        case SIMPLIFIED_CHINESE:
            return true;
        case TRADITIONAL_CHINESE:
        case JAPANESE:
        default:
            return false;
        }
    }

    @Override
    public IElementDoc getXjdoc() {
        return xjdoc;
    }

    @Override
    public void setXjdoc(IElementDoc xjdoc) {
        this.xjdoc = xjdoc;
    }

    static Set<String> attrNames = new LinkedHashSet<String>(Arrays.asList("code"));

    @Override
    public Set<String> getAttributeNames() {
        return attrNames;
    }

    @Override
    public boolean isAttributePresent(String name) {
        return attrNames.contains(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String attributeName) {
        Object value = null;
        switch (attributeName) {
        case "code":
            value = code;
        }
        return (T) value;
    }

    @Override
    public <T> T getAttribute(String name, T defaultValue) {
        T value = getAttribute(name);
        return value != null ? value : defaultValue;
    }

    static {
        fn.injectFields(Language.class, false);
    }

}
