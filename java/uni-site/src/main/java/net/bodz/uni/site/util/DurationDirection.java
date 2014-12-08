package net.bodz.uni.site.util;

import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

public enum DurationDirection
        implements IXjdocAware {

    /**
     * just now
     *
     * <p lang="zh-cn">
     * 刚刚
     *
     * <p lang="ja">
     * ただ
     */
    NOW,

    /**
     * ago
     *
     * <p lang="zh-cn">
     * 以前
     *
     * <p lang="ja">
     * 前に
     */
    AGO,

    /**
     * in the future
     *
     * <p lang="zh-cn">
     * 以后
     *
     * <p lang="ja">
     * 後に
     */
    FUTURE,

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
        fn.injectFields(DurationDirection.class, false);
    }

}
