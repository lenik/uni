package net.bodz.uni.site;

import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.http.ctx.CurrentRequestScope;
import net.bodz.bas.i18n.LocaleScr;
import net.bodz.bas.site.BasicSiteServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;

public class UniSiteServerConfig
        extends BasicSiteServerConfig {

    ServletDescriptor imgLink;

    public UniSiteServerConfig() {
        configEnv();
        configServlets();
    }

    void configEnv() {
        LocaleScr localeCtl = LocaleScr.LOCALE;
        localeCtl.setTeller(new CurrentRequestScope());
    }

    void configServlets() {
        imgLink = addLocalLink("/img", "/mnt/istore/projects/design/img", 100);

        ServletDescriptor pathDispatch = addServlet(PathDispatchServlet.class, "/*");
        pathDispatch.setInitParam(PathDispatchServlet.ROOT_CLASS, UniSiteFromSrc.class.getName());
    }

}
