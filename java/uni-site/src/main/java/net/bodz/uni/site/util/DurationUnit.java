package net.bodz.uni.site.util;

import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

public enum DurationUnit
        implements IXjdocAware {

    /**
     * second
     *
     * <p lang="zh-cn">
     * 秒
     *
     * <p lang="ja">
     * 秒
     */
    SECOND,

    /**
     * minute
     *
     * <p lang="zh-cn">
     * 分钟
     *
     * <p lang="ja">
     * 分
     */
    MINUTE,

    /**
     * hour
     *
     * <p lang="zh-cn">
     * 小时
     *
     * <p lang="ja">
     * 時
     */
    HOUR,

    /**
     * day
     *
     * <p lang="zh-cn">
     * 天
     *
     * <p lang="ja">
     * 日
     */
    DAY,

    /**
     * month
     *
     * <p lang="zh-cn">
     * 月
     *
     * <p lang="ja">
     * 月
     */
    MONTH,

    /**
     * year
     *
     * <p lang="zh-cn">
     * 年
     *
     * <p lang="ja">
     * 年
     */
    YEAR,

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
        ClassDocLoader.injectFields(DurationUnit.class, false);
    }

}
