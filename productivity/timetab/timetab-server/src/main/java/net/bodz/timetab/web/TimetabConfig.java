package net.bodz.timetab.web;

import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.i18n.LocaleVars;
import net.bodz.bas.servlet.config.ServletDescriptor;
import net.bodz.bas.servlet.ctx.RequestScopeTeller;
import net.bodz.uni.echo._default.DefaultServerConfig;

public class TimetabConfig
        extends DefaultServerConfig {

    protected ServletDescriptor dispatching;

    public TimetabConfig() {
        configEnv();
        configServlets();
    }

    protected void configEnv() {
        LocaleVars localeCtl = LocaleVars.LOCALE;
        localeCtl.setTeller(new RequestScopeTeller());
    }

    protected void configServlets() {
        dispatching = addServlet(PathDispatchServlet.class, "/*");
        dispatching.setInitParam(PathDispatchServlet.ROOT_CLASS, TimetabResolver.class.getName());
    }

}
