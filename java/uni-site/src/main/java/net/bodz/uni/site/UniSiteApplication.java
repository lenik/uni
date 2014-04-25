package net.bodz.uni.site;

import javax.servlet.http.HttpSession;

import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.uni.site.user.Preferences;

public class UniSiteApplication
        extends UniSite {

    public Preferences getPreferences() {
        HttpSession session = CurrentHttpService.getSession();
        Preferences preferences = Preferences.fromSession(session);
        return preferences;
    }

}
