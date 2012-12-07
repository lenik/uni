package net.bodz.uni.echo.server;

import java.util.Map;

import javax.servlet.Servlet;

import org.eclipse.jetty.servlet.ServletHolder;


public class EchoContextHandler
        extends EchoServletContextHandler {

    final EchoServer server;

    public EchoContextHandler(EchoServer server) {
        super(SESSIONS | SECURITY);
        if (server == null)
            throw new NullPointerException("server");
        this.server = server;
    }

    public EchoServer getEchoServer() {
        return server;
    }

    public synchronized void addInitParam(String name, String value) {
        Map<String, String> _initParams = getInitParams();
        _initParams.put(name, value);
    }

    @Override
    public ServletHolder addServlet(Class<? extends Servlet> servlet, String pathSpec) {
        // return super.addServlet(servlet, pathSpec);
        if (pathSpec == null) {
            ServletHolder holder = new ServletHolder(servlet);
            holder.setInitOrder(0);
            _servletHandler.addServlet(holder);
            return holder;
        } else {
            return _servletHandler.addServletWithMapping(servlet.getName(), pathSpec);
        }
    }

    @Override
    public ServletHolder addServlet(String className, String pathSpec) {
        // return super.addServlet(className, pathSpec);
        if (pathSpec == null) {
            ServletHolder holder = new ServletHolder((Servlet) null);
            holder.setName(className + "-" + holder.hashCode());
            holder.setClassName(className);
            holder.setInitOrder(0);
            _servletHandler.addServlet(holder);
            return holder;
        } else {
            return _servletHandler.addServletWithMapping(className, pathSpec);
        }
    }

}
