package net.bodz.bas.c.javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import net.bodz.bas.t.model.AbstractProxy;

/**
 * @see HttpServletRequestWrapper
 */
@Deprecated
public class DecoratedHttpServletRequest
        extends AbstractProxy<HttpServletRequest>
        implements HttpServletRequest {

    public DecoratedHttpServletRequest(HttpServletRequest proxy) {
        super(proxy);
    }

    @Override
    public Object getAttribute(String name) {
        return getWrapped().getAttribute(name);
    }

    @Override
    public String getAuthType() {
        return getWrapped().getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
        return getWrapped().getCookies();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return getWrapped().getAttributeNames();
    }

    @Override
    public long getDateHeader(String name) {
        return getWrapped().getDateHeader(name);
    }

    @Override
    public String getCharacterEncoding() {
        return getWrapped().getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        getWrapped().setCharacterEncoding(env);
    }

    @Override
    public String getHeader(String name) {
        return getWrapped().getHeader(name);
    }

    @Override
    public int getContentLength() {
        return getWrapped().getContentLength();
    }

    @Override
    public String getContentType() {
        return getWrapped().getContentType();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return getWrapped().getHeaders(name);
    }

    @Override
    public ServletInputStream getInputStream()
            throws IOException {
        return getWrapped().getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return getWrapped().getParameter(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return getWrapped().getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {
        return getWrapped().getIntHeader(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return getWrapped().getParameterNames();
    }

    @Override
    public String getMethod() {
        return getWrapped().getMethod();
    }

    @Override
    public String[] getParameterValues(String name) {
        return getWrapped().getParameterValues(name);
    }

    @Override
    public String getPathInfo() {
        return getWrapped().getPathInfo();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return getWrapped().getParameterMap();
    }

    @Override
    public String getProtocol() {
        return getWrapped().getProtocol();
    }

    @Override
    public String getPathTranslated() {
        return getWrapped().getPathTranslated();
    }

    @Override
    public String getScheme() {
        return getWrapped().getScheme();
    }

    @Override
    public String getContextPath() {
        return getWrapped().getContextPath();
    }

    @Override
    public String getServerName() {
        return getWrapped().getServerName();
    }

    @Override
    public int getServerPort() {
        return getWrapped().getServerPort();
    }

    @Override
    public BufferedReader getReader()
            throws IOException {
        return getWrapped().getReader();
    }

    @Override
    public String getQueryString() {
        return getWrapped().getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return getWrapped().getRemoteUser();
    }

    @Override
    public String getRemoteAddr() {
        return getWrapped().getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return getWrapped().getRemoteHost();
    }

    @Override
    public boolean isUserInRole(String role) {
        return getWrapped().isUserInRole(role);
    }

    @Override
    public void setAttribute(String name, Object o) {
        getWrapped().setAttribute(name, o);
    }

    @Override
    public Principal getUserPrincipal() {
        return getWrapped().getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {
        return getWrapped().getRequestedSessionId();
    }

    @Override
    public void removeAttribute(String name) {
        getWrapped().removeAttribute(name);
    }

    @Override
    public String getRequestURI() {
        return getWrapped().getRequestURI();
    }

    @Override
    public Locale getLocale() {
        return getWrapped().getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return getWrapped().getLocales();
    }

    @Override
    public StringBuffer getRequestURL() {
        return getWrapped().getRequestURL();
    }

    @Override
    public boolean isSecure() {
        return getWrapped().isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return getWrapped().getRequestDispatcher(path);
    }

    @Override
    public String getServletPath() {
        return getWrapped().getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return getWrapped().getSession(create);
    }

    @Deprecated
    @Override
    public String getRealPath(String path) {
        return getWrapped().getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return getWrapped().getRemotePort();
    }

    @Override
    public String getLocalName() {
        return getWrapped().getLocalName();
    }

    @Override
    public HttpSession getSession() {
        return getWrapped().getSession();
    }

    @Override
    public String getLocalAddr() {
        return getWrapped().getLocalAddr();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return getWrapped().isRequestedSessionIdValid();
    }

    @Override
    public int getLocalPort() {
        return getWrapped().getLocalPort();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return getWrapped().isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return getWrapped().isRequestedSessionIdFromURL();
    }

    @Deprecated
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return getWrapped().isRequestedSessionIdFromUrl();
    }

    // Java Servlet 3.0
    @Override
    public ServletContext getServletContext() {
        return getWrapped().getServletContext();
    }

    @Override
    public AsyncContext startAsync()
            throws IllegalStateException {
        return getWrapped().startAsync();
    }

    @Override
    public boolean authenticate(HttpServletResponse response)
            throws IOException, ServletException {
        return getWrapped().authenticate(response);
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return getWrapped().startAsync(servletRequest, servletResponse);
    }

    @Override
    public void login(String username, String password)
            throws ServletException {
        getWrapped().login(username, password);
    }

    @Override
    public void logout()
            throws ServletException {
        getWrapped().logout();
    }

    @Override
    public Collection<Part> getParts()
            throws IOException, ServletException {
        return getWrapped().getParts();
    }

    @Override
    public boolean isAsyncStarted() {
        return getWrapped().isAsyncStarted();
    }

    @Override
    public Part getPart(String name)
            throws IOException, ServletException {
        return getWrapped().getPart(name);
    }

    @Override
    public boolean isAsyncSupported() {
        return getWrapped().isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return getWrapped().getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return getWrapped().getDispatcherType();
    }

}
