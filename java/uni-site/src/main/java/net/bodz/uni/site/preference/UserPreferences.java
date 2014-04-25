package net.bodz.uni.site.preference;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.http.HttpSession;

public class UserPreferences
        implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ATTRIBUTE = UserPreferences.class.getSimpleName();

    Theme theme = Theme.CYAN;
    Language language = Language.ENGLISH;

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        if (theme == null)
            throw new NullPointerException("theme");
        this.theme = theme;
    }

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

    public static UserPreferences fromSession(HttpSession session) {
        UserPreferences preferences = (UserPreferences) session.getAttribute(ATTRIBUTE);
        if (preferences == null) {
            preferences = new UserPreferences();
            session.setAttribute(ATTRIBUTE, preferences);
        }
        return preferences;
    }

}
