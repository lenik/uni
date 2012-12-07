package net.bodz.bas.c.javax.servlet.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @see ThreadServletRequestListener
 * @see ThreadServletResponseListener
 */
public class ThreadServletContext {

    static final ThreadLocal<HttpServletRequest> threadLocalRequests = new ThreadLocal<HttpServletRequest>();
    static final ThreadLocal<HttpServletResponse> threadLocalResponses = new ThreadLocal<HttpServletResponse>();

    public static HttpServletRequest getRequestOpt() {
        return threadLocalRequests.get();
    }

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = getRequestOpt();

        if (request == null)
            throw new IllegalStateException("Not in an http-request scope");

        return request;
    }

    public static void setRequest(HttpServletRequest request) {
        threadLocalRequests.set(request);
    }

    public static HttpServletResponse getResponseOpt() {
        return threadLocalResponses.get();
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = getResponseOpt();

        if (response == null)
            throw new IllegalStateException("Not in an http-request scope");

        return threadLocalResponses.get();
    }

    public static void setResponse(HttpServletResponse response) {
        threadLocalResponses.set(response);
    }

    public static HttpSession getSessionOpt() {
        HttpServletRequest request = getRequestOpt();
        if (request == null)
            return null;
        return request.getSession();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletContext getServletContextOpt() {
        HttpSession session = getSessionOpt();
        if (session == null)
            return null;
        return session.getServletContext();
    }

    public static ServletContext getServletContext() {
        return getSession().getServletContext();
    }

    public static ServletContext getApplication() {
        return getServletContext();
    }

    public static synchronized void escape(Runnable runnable) {
        HttpServletRequest request = getRequestOpt();
        try {
            runnable.run();
        } finally {
            setRequest(request);
        }
    }

}
