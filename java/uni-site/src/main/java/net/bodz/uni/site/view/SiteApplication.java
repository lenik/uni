package net.bodz.uni.site.view;

import javax.servlet.http.HttpSession;

import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Site;

public class SiteApplication
        extends Site {

    public Preferences getPreferences() {
        HttpSession session = CurrentHttpService.getSession();
        Preferences preferences = Preferences.fromSession(session);
        return preferences;
    }

}
