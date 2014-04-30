package net.bodz.uni.site.model;

import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

/**
 * See: <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 language code</a>.
 */
public enum Language
        implements IXjdocAware {

    /**
     * English
     */
    ENGLISH("en"),

    /**
     * 中文（简体）
     */
    SIMPLIFIED_CHINESE("zh-cn"),

    /**
     * 日本語
     */
    JAPANESE("ja"),

    ;

    private String code;
    private IElementDoc xjdoc;

    private Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public IElementDoc getXjdoc() {
        return xjdoc;
    }

    @Override
    public void setXjdoc(IElementDoc xjdoc) {
        this.xjdoc = xjdoc;
    }

    static {
        ClassDocLoader.injectFields(Language.class, false);
    }

}
