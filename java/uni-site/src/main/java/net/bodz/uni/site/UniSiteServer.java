package net.bodz.uni.site;

import net.bodz.bas.http.ctx.CurrentRequestContextTeller;
import net.bodz.bas.i18n.LocaleCtl;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.server.EchoServer;

public class UniSiteServer
        extends BasicCLI {

    public UniSiteServer() {
        LocaleCtl localeCtl = LocaleCtl.LOCALE;
        localeCtl.setTeller(new CurrentRequestContextTeller());
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        EchoServerConfig config = new UniSiteServerConfig();
        EchoServer server = new EchoServer(config);
        server.start();
    }

    public static void main(String[] args)
            throws Exception {
        new UniSiteServer().execute(args);
    }

}
