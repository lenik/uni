package net.bodz.uni.site.model;

import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.IJavaElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

public enum Theme
        implements IXjdocAware {

    /**
     * Cyan
     *
     * <p lang="zh-cn">
     * 天使蓝
     */
    CYAN("cyan"),

    /**
     * Pink
     *
     * <p lang="zh-cn">
     * 天使粉
     */
    PINK("pink"),

    ;

    String suffix;
    IJavaElementDoc xjdoc;

    private Theme(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
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
        ClassDocLoader.injectFields(Theme.class, false);
    }

}
