package net.bodz.uni.echo.test;

import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.test.junit.JUnitApp;
import net.bodz.uni.echo.client.EchoClient;
import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.server.EchoServer;

public abstract class EchoTestApp
        extends JUnitApp<EchoTestApp> {

    static Logger logger = LoggerFactory.getLogger(EchoTestApp.class);

    protected final EchoTestServer server;
    protected final EchoServerConfig config;
    protected final EchoClient client;

    private boolean managedServerLifecycle;

    public EchoTestApp() {
        this.config = createConfig();
        // configure(config);

        server = new EchoTestServer(this, config);
        client = new EchoClient(server);

        managedServerLifecycle = getClass().isAnnotationPresent(ManagedServerLifecycle.class);
    }

    public static EchoTestApp fromContext(ServletContext servletContext) {
        EchoServer server = EchoServer.fromContext(servletContext);
        if (server instanceof EchoTestServer)
            return ((EchoTestServer) server).testApp;
        else
            return null;
    }

    public EchoServer getServer() {
        return server;
    }

    public EchoClient getClient() {
        return client;
    }

    public EchoServerConfig getConfig() {
        return config;
    }

    protected EchoServerConfig createConfig() {
        return new TestServerConfig();
    }

    @Before
    public void setUpServer()
            throws Exception {
        if (managedServerLifecycle)
            server.start();
    }

    @After
    public void tearDownServer()
            throws Exception {
        if (managedServerLifecycle)
            server.stop();
    }

    public EchoClient makeClient()
            throws Exception {
        final EchoTestApp app = this; // assemble();

        app.server.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    app.server.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
        return app.client;
    }

}