package net.bodz.uni.site;

import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.repr.path.PathDispatchServlet;
import net.bodz.uni.echo._default.DefaultServerConfig;
import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.server.EchoServer;

public class UniSiteWebServer
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        EchoServerConfig config = new DefaultServerConfig();
        EchoServer server = new EchoServer(config);

        PathDispatchServlet.startObject = new UniSite();
        config.addServlet(PathDispatchServlet.class, "/*");

        server.start();
    }

    public static void main(String[] args)
            throws Exception {
        new UniSiteWebServer().execute(args);
    }

}
