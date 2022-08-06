package net.bodz.uni.echo.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.echo.resource.IResourceProvider;

public class EchoServletContextHandler
        extends ServletContextHandler {

    static final Logger logger = LoggerFactory.getLogger(EchoServletContextHandler.class);

    private EchoServer echoServer;
    private IResourceProvider resourceProvider;

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

        if (path == null || !path.startsWith("/"))
            throw new MalformedURLException(path);

        URL resourceUrl;
        try {
            String resourcePath = path;
            while (resourcePath.startsWith("/"))
                resourcePath = resourcePath.substring(1);
            resourceUrl = resourceProvider.getResource(resourcePath);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

        // Not in search-bases, fallback to the default one (which is resource-base based).
        if (resourceUrl == null)
            return super.getResource(path);

        Resource resource;
        resource = Resource.newResource(resourceUrl);
        return resource;
    }

}
