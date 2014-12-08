package net.bodz.uni.site.model;

import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

/**
 * Theme
 *
 * 风格
 */
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
    CYAN,

    /**
     * Pink
     *
     * <p lang="ja">
     * 天使のピンク
     *
     * <p lang="zh-cn">
     * 天使粉
     */
    PINK,

    ;

    private IElementDoc xjdoc;

    @Override
    public IElementDoc getXjdoc() {
        return xjdoc;
    }

    @Override
    public void setXjdoc(IElementDoc xjdoc) {
        this.xjdoc = xjdoc;
    }

    static {
        fn.injectFields(Theme.class, false);
    }

}
