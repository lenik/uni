package net.bodz.uni.echo.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import net.bodz.bas.c.object.UseNet;
import net.bodz.bas.err.DuplicatedKeyException;
import net.bodz.bas.util.order.PrioritySortedLists;

public class EchoServerConfig {

    public static final int PRIORITY_FORCE = -10000;
    public static final int PRIORITY_HIGH = -100;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_LOW = 100;
    public static final int PRIORITY_FALLBACK = 10000;

    String hostName;
    int portNumber;
    String contextPath = "";

    List<String> welcomeFiles;

    UseNet<IPluginDescriptor> pluginNet;
    PluginDescriptorComparator pluginCmp;
    Map<String, ServletDescriptor> servletMap;
    Map<String, FilterDescriptor> filterMap;
    int servletIndex;
    int filterIndex;

    List<IServletContextListener> servletContextListeners;
    List<IServletRequestListener> servletRequestListeners;
    List<IServletContextAttributeListener> servletContextAttributeListeners;
    List<IServletRequestAttributeListener> servletRequestAttributeListeners;

    List<IHttpSessionListener> sessionListeners;
    List<IHttpSessionAttributeListener> sessionAttributeListeners;
    List<IHttpSessionActivationListener> sessionActivationListeners;
    List<IHttpSessionBindingListener> sessionBindingListeners;

    public EchoServerConfig() {
        welcomeFiles = new ArrayList<>();

        pluginNet = new UseNet<>();
        pluginCmp = new PluginDescriptorComparator(pluginNet);
        servletMap = new TreeMap<>();
        filterMap = new TreeMap<>();

        servletContextListeners = new ArrayList<IServletContextListener>();
        servletRequestListeners = new ArrayList<IServletRequestListener>();
        servletContextAttributeListeners = new ArrayList<IServletContextAttributeListener>();
        servletRequestAttributeListeners = new ArrayList<IServletRequestAttributeListener>();

        sessionListeners = new ArrayList<IHttpSessionListener>();
        sessionAttributeListeners = new ArrayList<IHttpSessionAttributeListener>();
        sessionActivationListeners = new ArrayList<IHttpSessionActivationListener>();
        sessionBindingListeners = new ArrayList<IHttpSessionBindingListener>();
    }

    public String getHostName(String fallback) {
        if (hostName != null)
            return hostName;
        else
            return fallback;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        if (contextPath == null)
            throw new NullPointerException("contextPath");
        if (!contextPath.isEmpty())
            if (contextPath.startsWith("/"))
                throw new IllegalArgumentException("Context path must be empty or start with slash.");
        this.contextPath = contextPath;
    }

    public URL toURL(String href)
            throws IOException {
        String urlString;
        if (href.contains("://"))
            urlString = href;
        else if (href.startsWith("/"))
            throw new IllegalArgumentException(//
                    "Illegal href: expected either absolute URL or context-relative href: " + href);
        else {
            String contextPath = getContextPath();
            String prefix = "http://" + getHostName("localhost") + ":" + getPortNumber() + contextPath;
            urlString = prefix + "/" + href;
        }

        URL url = new URL(urlString);
        return url;
    }

    public List<String> getWelcomeFiles() {
        return welcomeFiles;
    }

    public void addWelcomeFile(String welcomeFile) {
        if (welcomeFile == null)
            throw new NullPointerException("welcomeFile");
        if (!welcomeFiles.contains(welcomeFile))
            welcomeFiles.add(welcomeFile);
    }

    public void removeWelcomeFile(String welcomeFile) {
        if (welcomeFile == null)
            throw new NullPointerException("welcomeFile");
        welcomeFiles.remove(welcomeFile);
    }

    public Map<String, ServletDescriptor> getServletMap() {
        return servletMap;
    }

    public Map<String, FilterDescriptor> getFilterMap() {
        return filterMap;
    }

    public List<ServletDescriptor> getServlets() {
        List<ServletDescriptor> list = new ArrayList<>(servletMap.size());
        list.addAll(servletMap.values());
        Collections.sort(list, pluginCmp);
        return list;
    }

    public List<FilterDescriptor> getFilters() {
        List<FilterDescriptor> list = new ArrayList<>(filterMap.size());
        list.addAll(filterMap.values());
        Collections.sort(list, pluginCmp);
        return list;
    }

    public ServletDescriptor addServlet(Class<? extends Servlet> servletClass, String mapping) {
        ServletDescriptor servletConfig = new ServletDescriptor(servletClass);
        servletConfig.addMapping(mapping);
        addServlet(servletConfig);
        return servletConfig;
    }

    public void addServlet(ServletDescriptor servletConfig) {
        if (servletConfig == null)
            throw new NullPointerException("servletConfig");

        String id = servletConfig.getId();
        ServletDescriptor old = servletMap.get(id);
        if (old != null)
            throw new DuplicatedKeyException(id);

        // pluginNet.invlink(servletConfig, dependencies);
        servletConfig.setIndex(++servletIndex);
        servletMap.put(servletConfig.getId(), servletConfig);
    }

    public FilterDescriptor addFilter(Class<? extends Filter> filterClass, String mapping) {
        FilterDescriptor filterConfig = new FilterDescriptor(filterClass);
        filterConfig.addMapping(mapping);
        addFilter(filterConfig);
        return filterConfig;
    }

    public void addFilter(FilterDescriptor filterConfig) {
        if (filterConfig == null)
            throw new NullPointerException("filterConfig");

        String id = filterConfig.getId();
        FilterDescriptor old = filterMap.get(id);
        if (old != null)
            throw new DuplicatedKeyException(id);

        // filterNet.invlink(filterConfig, dependencies);
        filterConfig.setIndex(++filterIndex);
        filterMap.put(filterConfig.getId(), filterConfig);
    }

    // Event Listeners

    public List<IServletContextListener> getServletContextListeners() {
        return servletContextListeners;
    }

    public void addServletContextListener(IServletContextListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(servletContextListeners, listener);
    }

    public void removeServletContextListener(IServletContextListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(servletContextListeners, listener);
    }

    public List<IServletRequestListener> getServletRequestListeners() {
        return servletRequestListeners;
    }

    public void addServletContextListener(IServletRequestListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(servletRequestListeners, listener);
    }

    public void removeServletContextListener(IServletRequestListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(servletRequestListeners, listener);
    }

    public List<IServletContextAttributeListener> getServletContextAttributeListeners() {
        return servletContextAttributeListeners;
    }

    public void addServletContextAttributeListener(IServletContextAttributeListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(servletContextAttributeListeners, listener);
    }

    public void removeServletContextAttributeListener(IServletContextAttributeListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(servletContextAttributeListeners, listener);
    }

    public List<IServletRequestAttributeListener> getServletRequestAttributeListeners() {
        return servletRequestAttributeListeners;
    }

    public void addServletRequestAttributeListener(IServletRequestAttributeListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(servletRequestAttributeListeners, listener);
    }

    public void removeServletRequestAttributeListener(IServletRequestAttributeListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(servletRequestAttributeListeners, listener);
    }

    public List<IHttpSessionListener> getSessionListeners() {
        return sessionListeners;
    }

    public void addSessionListener(IHttpSessionListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(sessionListeners, listener);
    }

    public void removeSessionListener(IHttpSessionListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(sessionListeners, listener);
    }

    public List<IHttpSessionAttributeListener> getSessionAttributeListeners() {
        return sessionAttributeListeners;
    }

    public void addHttpSessionAttributeListener(IHttpSessionAttributeListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(sessionAttributeListeners, listener);
    }

    public void removeHttpSessionAttributeListener(IHttpSessionAttributeListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(sessionActivationListeners, listener);
    }

    public List<IHttpSessionActivationListener> getSessionActivationListeners() {
        return sessionActivationListeners;
    }

    public void addHttpSessionActivationListener(IHttpSessionActivationListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(sessionActivationListeners, listener);
    }

    public void removeHttpSessionActivationListener(IHttpSessionActivationListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(sessionActivationListeners, listener);
    }

    public List<IHttpSessionBindingListener> getSessionBindingListeners() {
        return sessionBindingListeners;
    }

    public void addHttpSessionBindingListener(IHttpSessionBindingListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.add(sessionBindingListeners, listener);
    }

    public void removeHttpSessionBindingListener(IHttpSessionBindingListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");
        PrioritySortedLists.remove(sessionBindingListeners, listener);
    }

}
