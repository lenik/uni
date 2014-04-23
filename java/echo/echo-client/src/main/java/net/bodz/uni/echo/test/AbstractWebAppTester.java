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

public abstract class AbstractWebAppTester
        extends JUnitApp<AbstractWebAppTester> {

    static Logger logger = LoggerFactory.getLogger(AbstractWebAppTester.class);

    public static final String TESTER_ATTRIBUTE = "WebAppTester";

    protected final EchoServer server;
    protected final EchoServerConfig config;
    protected final EchoClient client;

    private boolean managedServerLifecycle;

    public AbstractWebAppTester() {
        this.config = createConfig();
        // configure(config);

        server = new EchoServer(config);
        server.setAttribute(TESTER_ATTRIBUTE, this);
        client = new EchoClient(server);

        managedServerLifecycle = getClass().isAnnotationPresent(ManagedServerLifecycle.class);
    }

    public static AbstractWebAppTester fromContext(ServletContext servletContext) {
        EchoServer server = EchoServer.fromContext(servletContext);
        return (AbstractWebAppTester) server.getAttribute(TESTER_ATTRIBUTE);
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
        final AbstractWebAppTester app = this; // assemble();

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