package net.bodz.uni.site.model;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.http.HttpSession;

public class Preferences
        implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ATTRIBUTE = Preferences.class.getSimpleName();

    Theme theme = Theme.CYAN;
    Language language = Language.ENGLISH;

    /**
     * @label Theme
     * @label.ja 様式
     * @label.zh.cn 风格
     */
    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        if (theme == null)
            throw new NullPointerException("theme");
        this.theme = theme;
    }

    /**
     * Set theme by name.
     *
     * @throws IllegalArgumentException
     *             If theme name isn't defined.
     */
    public void setTheme(String themeName) {
        if (themeName == null)
            throw new NullPointerException("themeName");
        setTheme(Theme.valueOf(themeName));
    }

    /**
     * @label Language
     * @label.ja 言語
     * @label.zh.cn 语言
     */
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        if (language == null)
            throw new NullPointerException("language");
        this.language = language;

        // TODO Set the locale per-request.
        String code = language.getCode();
        Locale locale = Locale.forLanguageTag(code);
        Locale.setDefault(locale);
    }

    /**
     * Set theme by name.
     *
     * @throws IllegalArgumentException
     *             If theme name isn't defined.
     */
    public void setLanguage(String languageName) {
        if (languageName == null)
            throw new NullPointerException("languageName");
        setLanguage(Language.valueOf(languageName));
    }

    public static Preferences fromSession(HttpSession session) {
        Preferences preferences = (Preferences) session.getAttribute(ATTRIBUTE);
        if (preferences == null) {
            preferences = new Preferences();
            session.setAttribute(ATTRIBUTE, preferences);
        }
        return preferences;
    }

}
