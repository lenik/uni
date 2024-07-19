package net.bodz.uni.echo.test;

import jakarta.servlet.ServletContext;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.servlet.config.ServletContextConfig;
import net.bodz.uni.echo._default.DefaultServerConfig;
import net.bodz.uni.echo.client.EchoClient;
import net.bodz.uni.echo.server.EchoServer;

public abstract class AbstractWebAppRunner {

    static Logger logger = LoggerFactory.getLogger(AbstractWebAppRunner.class);

    public static final String RUNNER_ATTRIBUTE = "WebAppRunner";

    protected final EchoServer server;
    protected final ServletContextConfig config;
    protected final EchoClient client;

    public AbstractWebAppRunner() {
        this.config = createConfig();
        // configure(config);

        server = new EchoServer(config);
        server.setAttribute(RUNNER_ATTRIBUTE, this);
        client = new EchoClient(server);
    }

    public static AbstractWebAppRunner fromContext(ServletContext servletContext) {
        EchoServer server = EchoServer.fromContext(servletContext);
        return (AbstractWebAppRunner) server.getAttribute(RUNNER_ATTRIBUTE);
    }

    public EchoServer getServer() {
        return server;
    }

    public EchoClient getClient() {
        return client;
    }

    public ServletContextConfig getConfig() {
        return config;
    }

    protected ServletContextConfig createConfig() {
        return new DefaultServerConfig();
    }

    public EchoClient makeClient()
            throws Exception {
        final AbstractWebAppRunner app = this; // assemble();

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