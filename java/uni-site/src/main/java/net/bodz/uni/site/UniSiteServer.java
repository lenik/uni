package net.bodz.uni.site;

import net.bodz.bas.http.config.ServletContextConfig;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.uni.echo.server.EchoServer;

public class UniSiteServer
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        ServletContextConfig config = new UniSiteServerConfig();
        EchoServer server = new EchoServer(config);
        server.start();
    }

    public static void main(String[] args)
            throws Exception {
        new UniSiteServer().execute(args);
    }

}
