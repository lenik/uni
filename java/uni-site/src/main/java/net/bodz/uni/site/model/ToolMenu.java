package net.bodz.uni.site.model;

import net.bodz.uni.site.UniSite;

/**
 * @style.class ui-menubox
 */
public class ToolMenu {

    private UniSite app;

    public ToolMenu(UniSite app) {
        if (app == null)
            throw new NullPointerException("app");
        this.app = app;
    }

    /**
     * @label Theme
     * @label.ja 様式
     * @label.zh.cn 风格
     */
    public Theme getTheme() {
        return app.getPreferences().getTheme();
    }

    public void setTheme(Theme theme) {
        app.getPreferences().setTheme(theme);
    }

    /**
     * @label Language
     * @label.ja 言語
     * @label.zh.cn 语言
     */
    public Language getLanguage() {
        return app.getPreferences().getLanguage();
    }

    public void setLanguage(Language language) {
        app.getPreferences().setLanguage(language);
    }

}
