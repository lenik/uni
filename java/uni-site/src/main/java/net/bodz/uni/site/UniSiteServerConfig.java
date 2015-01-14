package net.bodz.uni.site;

import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.http.config.ServletDescriptor;
import net.bodz.bas.http.ctx.CurrentRequestScope;
import net.bodz.bas.i18n.LocaleScr;
import net.bodz.uni.echo._default.DefaultServerConfig;

public class UniSiteServerConfig
        extends DefaultServerConfig {

    public UniSiteServerConfig() {
        configEnv();
        configServlets();
    }

    void configEnv() {
        LocaleScr localeCtl = LocaleScr.LOCALE;
        localeCtl.setTeller(new CurrentRequestScope());
    }

    void configServlets() {
        ServletDescriptor pathDispatch = addServlet(PathDispatchServlet.class, "/*");
        pathDispatch.setInitParam(PathDispatchServlet.ROOT_CLASS, UniSiteFromSrc.class.getName());
    }

}
