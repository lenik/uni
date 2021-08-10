package net.bodz.uni.site;

import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.i18n.LocaleVars;
import net.bodz.bas.servlet.config.ServletDescriptor;
import net.bodz.bas.servlet.ctx.RequestScopeTeller;
import net.bodz.uni.echo._default.DefaultServerConfig;

public class Uni14Config
        extends DefaultServerConfig {

    public Uni14Config() {
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
