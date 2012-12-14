package net.bodz.uni.echo.config;

import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

public class EchoServerConfigAdapter
        implements ServletContextListener, //
        ServletContextAttributeListener, //
        ServletRequestListener, //
        ServletRequestAttributeListener, //
        HttpSessionListener, //
        HttpSessionAttributeListener, //
        HttpSessionActivationListener, //
        HttpSessionBindingListener {

    private final EchoServerConfig config;

    public EchoServerConfigAdapter(EchoServerConfig config) {
        if (config == null)
            throw new NullPointerException("config");
        this.config = config;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        for (IServletContextListener listener : config.getServletContextListeners()) {
            if (listener.isIncluded(event))
                listener.contextInitialized(event);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        List<IServletContextListener> listeners = config.getServletContextListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IServletContextListener listener = listeners.get(i);
            if (listener.isIncluded(event))
                listener.contextDestroyed(event);
        }
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        for (IServletContextAttributeListener listener : config.getServletContextAttributeListeners()) {
            listener.attributeAdded(event);
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        List<IServletContextAttributeListener> listeners = config.getServletContextAttributeListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IServletContextAttributeListener listener = listeners.get(i);
            listener.attributeRemoved(event);
        }
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        for (IServletContextAttributeListener listener : config.getServletContextAttributeListeners()) {
            listener.attributeReplaced(event);
        }
    }

    static boolean generateSessionEventFromRequestEvent = false;
    public static final String SESSION_CREATED_KEY = "echo:sessionCreated";

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        for (IServletRequestListener listener : config.getServletRequestListeners()) {
            listener.requestInitialized(event);
        }

        /**
         * Workaround: Jetty-6.1.x doesn't support http session listener.
         */
        if (generateSessionEventFromRequestEvent) {
            HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
            HttpSession session = request.getSession();
            if (session.getAttribute(SESSION_CREATED_KEY) == null) {
                session.setAttribute(SESSION_CREATED_KEY, true);
                HttpSessionEvent sessionEvent = new HttpSessionEvent(session);
                sessionCreated(sessionEvent);
            }
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        List<IServletRequestListener> listeners = config.getServletRequestListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IServletRequestListener listener = listeners.get(i);
            listener.requestDestroyed(event);
        }
    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent event) {
        for (IServletRequestAttributeListener listener : config.getServletRequestAttributeListeners()) {
            listener.attributeAdded(event);
        }
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent event) {
        List<IServletRequestAttributeListener> listeners = config.getServletRequestAttributeListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IServletRequestAttributeListener listener = listeners.get(i);
            listener.attributeRemoved(event);
        }
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent event) {
        for (IServletRequestAttributeListener listener : config.getServletRequestAttributeListeners()) {
            listener.attributeReplaced(event);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        for (IHttpSessionListener listener : config.getSessionListeners()) {
            listener.sessionCreated(event);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        List<IHttpSessionListener> listeners = config.getSessionListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IHttpSessionListener listener = listeners.get(i);
            listener.sessionDestroyed(event);
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        for (IHttpSessionAttributeListener listener : config.getSessionAttributeListeners()) {
            listener.attributeAdded(event);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        List<IHttpSessionAttributeListener> listeners = config.getSessionAttributeListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IHttpSessionAttributeListener listener = listeners.get(i);
            listener.attributeRemoved(event);
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        for (IHttpSessionAttributeListener listener : config.getSessionAttributeListeners()) {
            listener.attributeReplaced(event);
        }
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        for (IHttpSessionBindingListener listener : config.getSessionBindingListeners()) {
            listener.valueBound(event);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        List<IHttpSessionBindingListener> listeners = config.getSessionBindingListeners();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            IHttpSessionBindingListener listener = listeners.get(i);
            listener.valueUnbound(event);
        }
    }

    @Override
    public void sessionWillPassivate(HttpSessionEvent event) {
        for (IHttpSessionActivationListener listener : config.getSessionActivationListeners()) {
            listener.sessionWillPassivate(event);
        }
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent event) {
        for (IHttpSessionActivationListener listener : config.getSessionActivationListeners()) {
            listener.sessionDidActivate(event);
        }
    }

}
