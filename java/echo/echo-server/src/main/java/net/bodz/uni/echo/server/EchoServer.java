package net.bodz.uni.echo.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;

import net.bodz.bas.servlet.config.FilterDescriptor;
import net.bodz.bas.servlet.config.ServletContextConfig;
import net.bodz.bas.servlet.config.ServletContextConfigAdapter;
import net.bodz.bas.servlet.config.ServletDescriptor;
import net.bodz.uni.echo.resource.DerivedResourceProvider;
import net.bodz.uni.echo.resource.IResourceProvider;
import net.bodz.uni.echo.resource.MountableResourceProvider;
import net.bodz.uni.echo.resource.ResourceProviders;
import net.bodz.uni.echo.resource.UnionResourceProvider;

public class EchoServer
        extends Server {

    ServletContextConfig config;
    ServletContextHandler servletContextHandler;
    IResourceProvider resourceProvider;

    boolean initialized;
    Thread shutdownThread;

    public EchoServer(ServletContextConfig config) {
        super();

        ServerConnector connector = buildConnector(config);
        setConnectors(new Connector[] { connector });

        this.config = config;

        try {
            buildResourceProvider();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        servletContextHandler = new EchoServletContextHandler(this);
        setHandler(servletContextHandler);

        shutdownThread = new Thread() {
            @Override
            public void run() {
                try {
                    EchoServer.this.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        };

        addLifeCycleListener(new LifeCycleListener());
    }

    ServerConnector buildConnector(ServletContextConfig config) {
        ServerConnector connector = new ServerConnector(this);
        String hostName = config.getHostName();
        int port = config.getPortNumber();

        if (hostName == null) {
            // Enable both IPv4 & IPv6 on localhost.
        } else {
            connector.setHost(hostName);
        }
        connector.setPort(port);
        return connector;
    }

    private void buildResourceProvider()
            throws IOException {
        UnionResourceProvider serverResources = ResourceProviders.scanInheritedClassResources(getClass(), true);
        serverResources.setPriority(ServletContextConfig.PRIORITY_LOW);

        MountableResourceProvider rootResourceProvider = new MountableResourceProvider("root");
        rootResourceProvider.setUnionAuto(true);
        rootResourceProvider.setUnionSorted(true);
        rootResourceProvider.mount("", serverResources);

        Map<String, String> extensionMap = config.getExtensionMap();
        if (extensionMap == null) {
            extensionMap = new HashMap<String, String>();
            config.setExtensionMap(extensionMap);
        }

        resourceProvider = new DerivedResourceProvider(rootResourceProvider, extensionMap);
    }

    /**
     * Returns servletContext.contextHandler.echoServer.
     */
    public static EchoServer fromContext(ServletContext servletContext) {
        if (servletContext instanceof ContextHandler.Context) {
            ContextHandler handler = ((ContextHandler.Context) servletContext).getContextHandler();
            if (handler instanceof EchoServletContextHandler)
                return ((EchoServletContextHandler) handler).getEchoServer();
        }
        return null;
    }

    public ServletContextConfig getConfig() {
        return config;
    }

    public ServletContextHandler getServletContextHandler() {
        return servletContextHandler;
    }

    public IResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    synchronized void init() {
        if (initialized)
            return;

        String contextPath = config.getContextPath();
        if (contextPath != null)
            servletContextHandler.setContextPath(contextPath);

        List<String> welcomeFiles = config.getWelcomeFiles();
        servletContextHandler.setWelcomeFiles(welcomeFiles.toArray(new String[0]));

        for (Entry<String, String> entry : config.getInitParamMap().entrySet())
            servletContextHandler.setInitParameter(entry.getKey(), entry.getValue());

        ServletContextConfigAdapter configAdapter = new ServletContextConfigAdapter(config);
        servletContextHandler.addEventListener(configAdapter);

        for (ServletDescriptor servlet : config.getServlets()) {
            ServletHolder holder = new ServletHolder(servlet.getServletClass());
            holder.setDisplayName(servlet.getDisplayName());
            holder.setInitParameters(servlet.getInitParamMap());
            holder.setInitOrder(servlet.getPriority());

            for (String pathSpec : servlet.getMappings())
                servletContextHandler.addServlet(holder, pathSpec);
        }

        for (FilterDescriptor filter : config.getFilters()) {
            FilterHolder holder = new FilterHolder(filter.getFilterClass());
            holder.setDisplayName(filter.getDisplayName());
            holder.setInitParameters(filter.getInitParamMap());
            holder.setAsyncSupported(filter.isSuspendable());

            for (String pathSpec : filter.getMappings())
                servletContextHandler.addFilter(holder, pathSpec, filter.getDispatcherTypes());
        }

        initialized = true;
    }

    @Override
    protected void doStart()
            throws Exception {
        init();

        super.doStart();

        if (config.getPortNumber() == 0) {
            for (Connector connector : getConnectors()) {
                if (connector instanceof ServerConnector) {
                    ServerConnector sc = (ServerConnector) connector;
                    int actualPort = sc.getLocalPort();
                    if (actualPort > 0) {
                        config.setPortNumber(actualPort);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void doStop()
            throws Exception {
        super.doStop();
    }

    class LifeCycleListener
            implements
                LifeCycle.Listener {

        @Override
        public void lifeCycleStarted(LifeCycle event) {
            Runtime.getRuntime().addShutdownHook(shutdownThread);
        }

        @Override
        public void lifeCycleStopped(LifeCycle event) {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
        }

        @Override
        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
        }

    }

}
