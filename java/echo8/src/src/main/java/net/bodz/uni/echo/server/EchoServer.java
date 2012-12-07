package net.bodz.uni.echo.server;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;

import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.config.EchoServerConfigAdapter;
import net.bodz.uni.echo.config.FilterDescriptor;
import net.bodz.uni.echo.config.ServletDescriptor;

public class EchoServer
        extends Server {

    EchoServerConfig config;
    EchoContextHandler servletContextHandler;

    public EchoServer(EchoServerConfig config) {
        super(config.getPortNumber());

        this.config = config;

        setSendServerVersion(false);

        servletContextHandler = new EchoContextHandler(this);
        setHandler(servletContextHandler);

        String contextPath = config.getContextPath();
        if (contextPath != null)
            servletContextHandler.setContextPath(contextPath);

        EchoServerConfigAdapter configAdapter = new EchoServerConfigAdapter(config);
        servletContextHandler.addEventListener(configAdapter);

        for (ServletDescriptor servlet : config.getServlets()) {
            ServletHolder holder = new ServletHolder(servlet.getServletClass());
            holder.setDisplayName(servlet.getDisplayName().toString());
            holder.setInitParameters(servlet.getInitParamMap());

            for (String pathSpec : servlet.getMappings())
                servletContextHandler.addServlet(holder, pathSpec);
        }

        for (FilterDescriptor filter : config.getFilters()) {
            FilterHolder holder = new FilterHolder(filter.getFilterClass());
            holder.setDisplayName(filter.getDisplayName().toString());
            holder.setInitParameters(filter.getInitParamMap());
            holder.setAsyncSupported(filter.isSuspendable());

            for (String pathSpec : filter.getMappings())
                servletContextHandler.addFilter(holder, pathSpec, filter.getDispatcherTypes());
        }

    }

    /**
     * Returns servletContext.contextHandler.echoServer.
     */
    public static EchoServer fromContext(ServletContext servletContext) {
        if (servletContext instanceof ContextHandler.Context) {
            ContextHandler handler = ((ContextHandler.Context) servletContext).getContextHandler();
            if (handler instanceof EchoContextHandler)
                return ((EchoContextHandler) handler).getEchoServer();
        }
        return null;
    }

    public EchoServerConfig getConfig() {
        return config;
    }

    public EchoContextHandler getServletContextHandler() {
        return servletContextHandler;
    }

    @Override
    protected void doStart()
            throws Exception {
        super.doStart();

        if (config.getPortNumber() == 0) {
            for (Connector connector : getConnectors()) {
                int actualPort = connector.getLocalPort();
                if (actualPort > 0) {
                    config.setPortNumber(actualPort);
                    break;
                }
            }
        }
    }

    @Override
    protected void doStop()
            throws Exception {
        super.doStop();
    }

}
