package net.bodz.uni.site.preference;

import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.IJavaElementDoc;
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
    IJavaElementDoc xjdoc;

    private Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public IJavaElementDoc getXjdoc() {
        return xjdoc;
    }

    @Override
    public void setXjdoc(IJavaElementDoc xjdoc) {
        this.xjdoc = xjdoc;
    }

    static {
        ClassDocLoader.injectFields(Language.class, false);
    }

}
