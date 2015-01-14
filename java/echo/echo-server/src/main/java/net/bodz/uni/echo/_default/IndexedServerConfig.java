package net.bodz.uni.echo._default;

import java.util.ServiceLoader;

import net.bodz.bas.c.javax.servlet.http.*;
import net.bodz.bas.http.config.ServletContextConfig;
import net.bodz.bas.http.config.ServletDescriptor;

public class IndexedServerConfig
        extends ServletContextConfig {

    public IndexedServerConfig() {
        for (IServletContextListener listener : ServiceLoader.load(IServletContextListener.class))
            addServletContextListener(listener);

        for (IServletContextAttributeListener listener : ServiceLoader.load(IServletContextAttributeListener.class))
            addServletContextAttributeListener(listener);

        // TODO PrototypeLoader...
        for (IHttpFilter filter : ServiceLoader.load(IHttpFilter.class)) {
            String mapping = filter.getPreferredMapping();
            addFilter(filter.getClass(), mapping);
        }

        for (IHttpSessionListener listener : ServiceLoader.load(IHttpSessionListener.class))
            addSessionListener(listener);

        for (IHttpSessionAttributeListener listener : ServiceLoader.load(IHttpSessionAttributeListener.class))
            addHttpSessionAttributeListener(listener);

        for (IHttpSessionActivationListener listener : ServiceLoader.load(IHttpSessionActivationListener.class))
            addHttpSessionActivationListener(listener);

        for (IHttpSessionBindingListener listener : ServiceLoader.load(IHttpSessionBindingListener.class))
            addHttpSessionBindingListener(listener);

        for (IServletRequestListener listener : ServiceLoader.load(IServletRequestListener.class))
            addServletRequestListener(listener);

        for (IServletRequestAttributeListener listener : ServiceLoader.load(IServletRequestAttributeListener.class))
            addServletRequestAttributeListener(listener);

        addServlet(Favicon.class, "/favicon.ico");

        // The wildcard * is needed, cuz they are class resources, not overlapped resources.
        addServlet(Logo.class, "/logo/*");

        ServletDescriptor unionServlet = addServlet(EchoResourceServlet.class, "/");
        unionServlet.setPriority(PRIORITY_FALLBACK);
        unionServlet.setInitParam("welcomeServlets", "true");
    }

}
