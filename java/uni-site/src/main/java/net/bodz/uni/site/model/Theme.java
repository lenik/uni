package net.bodz.uni.site.model;

import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

public enum Theme
        implements IXjdocAware {

    /**
     * Cyan
     *
     * <p lang="ja">
     * 天使の青
     *
     * <p lang="zh-cn">
     * 天使蓝
     */
    CYAN("cyan"),

    /**
     * Pink
     *
     * <p lang="ja">
     * 天使のピンク
     *
     * <p lang="zh-cn">
     * 天使粉
     */
    PINK("pink"),

    ;

    private String suffix;
    private IElementDoc xjdoc;

    private Theme(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
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
        ClassDocLoader.injectFields(Theme.class, false);
    }

}
