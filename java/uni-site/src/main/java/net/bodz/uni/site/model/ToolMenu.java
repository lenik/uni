package net.bodz.uni.site.model;

import net.bodz.uni.site.view.SiteApplication;

public class ToolMenu {

    private SiteApplication app;

    public ToolMenu(SiteApplication app) {
        if (app == null)
            throw new NullPointerException("app");
        this.app = app;
    }

    public Theme getTheme() {
        return app.getPreferences().getTheme();
    }

    public void setTheme(String themeName) {
        app.getPreferences().setTheme(themeName);
    }

    public Language getLanguage() {
        return app.getPreferences().getLanguage();
    }

    public void setLanguage(String languageName) {
        app.getPreferences().setLanguage(languageName);
    }

}
