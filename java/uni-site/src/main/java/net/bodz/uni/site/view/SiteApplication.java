package net.bodz.uni.site.view;

import java.io.File;

import javax.servlet.http.HttpSession;

import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Site;
import net.bodz.uni.site.model.ToolMenu;

public class SiteApplication
        extends Site {

    ToolMenu toolMenu;

    public SiteApplication(File rootDir) {
        super(rootDir);
        toolMenu = new ToolMenu(this);
    }

    public Preferences getPreferences() {
        HttpSession session = CurrentHttpService.getSession();
        Preferences preferences = Preferences.fromSession(session);
        return preferences;
    }

    public ToolMenu getToolMenu() {
        return toolMenu;
    }

}
