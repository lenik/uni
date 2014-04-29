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
     * @label English
     */
    ENGLISH("en"),

    /**
     * @label 中文（简体）
     */
    SIMPLIFIED_CHINESE("zh-cn"),

    /**
     * @label 日本語
     */
    JAPANESE("ja"),

    ;

    String code;
    IElementDoc xjdoc;

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
