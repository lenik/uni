package net.bodz.uni.echo.server;

import java.net.MalformedURLException;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.echo._default.MultiLayer;
import net.bodz.uni.echo.resource.IResourceProvider;

public class EchoServletContextHandler
        extends ServletContextHandler {

    static final Logger logger = LoggerFactory.getLogger(EchoServletContextHandler.class);

    private final EchoServer echoServer;
    private final IResourceProvider resourceProvider;

    public EchoServletContextHandler(EchoServer echoServer) {
        this(echoServer, SECURITY | SESSIONS);
    }

    public EchoServletContextHandler(EchoServer echoServer, int options) {
        super(options);
        this.echoServer = echoServer;
        this.resourceProvider = echoServer.getResourceProvider();
    }

    public EchoServer getEchoServer() {
        return echoServer;
    }

    @Override
    public Resource getResource(String path)
            throws MalformedURLException {
        logger.debug("get-resource: " + path);
        return MultiLayer.from(resourceProvider) //
                .fallback(super::getResource) //
                .convert(this::newResource) //
                .getResource(path);
    }

}
