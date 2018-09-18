package net.bodz.uni.echo._default;

import net.bodz.bas.c.javax.servlet.http.*;
import net.bodz.bas.c.type.IndexedTypes;
import net.bodz.bas.http.config.ServletContextConfig;
import net.bodz.bas.http.config.ServletDescriptor;
import net.bodz.bas.meta.codegen.IndexedTypeLoader;

@IndexedTypeLoader({//
IServletContextListener.class, //
        IServletContextAttributeListener.class, //
        IHttpFilter.class,//
        IHttpSessionListener.class, //
        IHttpSessionAttributeListener.class, //
        IHttpSessionActivationListener.class, //
        IHttpSessionBindingListener.class, //
        IServletRequestListener.class, //
        IServletRequestAttributeListener.class, //
})
public class IndexedServerConfig
        extends ServletContextConfig {

    public IndexedServerConfig() {
        for (IServletContextListener listener : IndexedTypes.loadInOrder(IServletContextListener.class))
            addServletContextListener(listener);

        for (IServletContextAttributeListener listener : IndexedTypes
                .loadInOrder(IServletContextAttributeListener.class))
            addServletContextAttributeListener(listener);

        // TODO PrototypeLoader...
        for (IHttpFilter filter : IndexedTypes.loadInOrder(IHttpFilter.class)) {
            String mapping = filter.getPreferredMapping();
            addFilter(filter.getClass(), mapping);
        }

        for (IHttpSessionListener listener : IndexedTypes.loadInOrder(IHttpSessionListener.class))
            addSessionListener(listener);

        for (IHttpSessionAttributeListener listener : IndexedTypes.loadInOrder(IHttpSessionAttributeListener.class))
            addHttpSessionAttributeListener(listener);

        for (IHttpSessionActivationListener listener : IndexedTypes.loadInOrder(IHttpSessionActivationListener.class))
            addHttpSessionActivationListener(listener);

        for (IHttpSessionBindingListener listener : IndexedTypes.loadInOrder(IHttpSessionBindingListener.class))
            addHttpSessionBindingListener(listener);

        for (IServletRequestListener listener : IndexedTypes.loadInOrder(IServletRequestListener.class))
            addServletRequestListener(listener);

        for (IServletRequestAttributeListener listener : IndexedTypes
                .loadInOrder(IServletRequestAttributeListener.class))
            addServletRequestAttributeListener(listener);

        addServlet(Favicon.class, "/favicon.ico");

        // The wildcard * is needed, cuz they are class resources, not overlapped resources.
        addServlet(Logo.class, "/logo/*");

        ServletDescriptor unionServlet = addServlet(EchoResourceServlet.class, "/");
        unionServlet.setPriority(PRIORITY_FALLBACK);
        unionServlet.setInitParam("welcomeServlets", "true");
    }

}
