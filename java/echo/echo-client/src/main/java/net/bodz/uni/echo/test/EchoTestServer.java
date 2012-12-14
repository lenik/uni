package net.bodz.uni.echo.test;

import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.server.EchoServer;

public class EchoTestServer
        extends EchoServer {

    EchoTestApp testApp;

    public EchoTestServer(EchoTestApp testApp, EchoServerConfig config) {
        super(config);
        this.testApp = testApp;
    }

}
