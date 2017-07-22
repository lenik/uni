package net.bodz.uni.site;

import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.http.config.ServletDescriptor;
import net.bodz.bas.http.ctx.RequestScopeTeller;
import net.bodz.bas.i18n.LocaleVars;
import net.bodz.uni.echo._default.DefaultServerConfig;

public class UniSiteServerConfig
        extends DefaultServerConfig {

    public UniSiteServerConfig() {
        configEnv();
        configServlets();
    }

    void configEnv() {
        LocaleVars localeCtl = LocaleVars.LOCALE;
        localeCtl.setTeller(new RequestScopeTeller());
    }

    void configServlets() {
        ServletDescriptor pathDispatch = addServlet(PathDispatchServlet.class, "/*");
        pathDispatch.setInitParam(PathDispatchServlet.ROOT_CLASS, DefaultUniSite.class.getName());
    }

}
