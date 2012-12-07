package net.bodz.bas.c.javax.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.c.java.util.Collections;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.sio.BCharOut;
import net.bodz.bas.sio.ITreeOut;
import net.bodz.bas.sio.TreeOutImpl;

public class ServletDiag {

    static <E extends Comparable<E>> List<E> sort(Enumeration<E> enm) {
        List<E> list = Collections.toList(enm);
        Collections.sort(list);
        return list;
    }

    /**
     * Dump the request information in the request/response instances.
     *
     * @return Always return <code>null</code>.
     */
    public static <T> T dump(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain");

        BCharOut buf = new BCharOut();
        ITreeOut out = TreeOutImpl.from(buf);

        out.println("Request: ");
        {
            out.enter();
            out.println("attributes: ");
            {
                out.enter();
                for (String attrName : sort((Enumeration<String>) req.getAttributeNames())) {
                    Object attrValue = req.getAttribute(attrName);
                    out.println(attrName + " = " + attrValue);
                }
                out.leave();
            }

            out.println("authType = " + req.getAuthType());
            out.println("characterEncoding = " + req.getCharacterEncoding());
            out.println("contentLength = " + req.getContentLength());
            out.println("contentType = " + req.getContentType());
            out.println("contextPath = " + req.getContextPath());
            out.println("cookies = ");
            {
                out.enter();
                for (Cookie cookie : req.getCookies()) {
                    out.println(cookie.getName());
                    {
                        out.enter();
                        out.println("class = " + cookie.getClass());
                        out.println("comment = " + cookie.getComment());
                        out.println("domain = " + cookie.getDomain());
                        out.println("maxAge = " + cookie.getMaxAge());
                        out.println("path = " + cookie.getPath());
                        out.println("secure = " + cookie.getSecure());
                        out.println("value = " + cookie.getValue());
                        out.println("version = " + cookie.getVersion());
                        out.leave();
                    }
                }
                out.leave();
            }

            out.println("headers: ");
            {
                out.enter();
                for (String headerName : Collections.toList((Enumeration<String>) req.getHeaderNames())) {
                    out.println(headerName + " = " + req.getHeader(headerName));
                }
                out.leave();
            }

            out.println("localAddr = " + req.getLocalAddr());
            out.println("localName = " + req.getLocalName());
            out.println("localPort = " + req.getLocalPort());

            out.println("locales: ");
            {
                out.enter();
                for (Locale locale : Collections.toList((Enumeration<Locale>) req.getLocales())) {
                    out.println(locale);
                }
                out.leave();
            }

            out.println("method = " + req.getMethod());

            out.println("parameters: ");
            {
                out.enter();
                for (String paramName : sort((Enumeration<String>) req.getParameterNames())) {
                    Object paramValue = req.getParameter(paramName);
                    out.println(paramName + " = " + paramValue);
                }
                out.leave();
            }

            out.println("pathInfo = " + req.getPathInfo());
            out.println("pathTranslated = " + req.getPathTranslated());
            out.println("protocol = " + req.getProtocol());
            out.println("queryString = " + req.getQueryString());
            out.println("remoteAddr = " + req.getRemoteAddr());
            out.println("remoteHost = " + req.getRemoteHost());
            out.println("remotePort = " + req.getRemotePort());
            out.println("remoteUser = " + req.getRemoteUser());
            out.println("requestURI = " + req.getRequestURI());
            out.println("requestURL = " + req.getRequestURL());
            out.println("requestedSessionId = " + req.getRequestedSessionId());
            out.println("scheme = " + req.getScheme());
            out.println("serverName = " + req.getServerName());
            out.println("serverPort = " + req.getServerPort());
            out.println("servletPath = " + req.getServletPath());
            out.println("userPrincipal = " + req.getUserPrincipal());

            out.leave();
        }
        out.println();

        out.println("Response: ");
        {
            out.enter();
            out.println("bufferSize = " + resp.getBufferSize());
            out.println("characterEncoding = " + resp.getCharacterEncoding());
            out.println("contentType = " + resp.getContentType());
            out.println("locale = " + resp.getLocale());
            out.leave();
        }
        out.println();

        HttpSession session = req.getSession();
        out.println("Session: ");
        {
            out.enter();
            out.println("attributes: ");
            {
                out.enter();
                for (String attrName : sort((Enumeration<String>) session.getAttributeNames())) {
                    Object attrValue = session.getAttribute(attrName);
                    out.println(attrName + " = " + attrValue);
                }
                out.leave();
            }
            out.println("creationTime = " + session.getCreationTime());
            out.println("id = " + session.getId());
            out.println("lastAccessedTime = " + session.getLastAccessedTime());
            out.println("maxInactiveInterval = " + session.getMaxInactiveInterval());
            out.leave();
        }
        out.println();

        ServletContext context = session.getServletContext();
        out.println("Servlet Context: ");
        {
            out.enter();
            out.println("attributes: ");
            {
                out.enter();
                for (String attrName : sort((Enumeration<String>) context.getAttributeNames())) {
                    Object attrValue = context.getAttribute(attrName);
                    out.println(attrName + " = " + attrValue);
                }
                out.leave();
            }

            out.println("contextPath = " + context.getContextPath());
            out.println("init parameters: ");
            {
                out.enter();
                for (String initParamName : sort((Enumeration<String>) context.getInitParameterNames())) {
                    Object initParamValue = context.getInitParameter(initParamName);
                    out.println(initParamName + " = " + initParamValue);
                }
                out.leave();
            }

            out.println("majorVersion = " + context.getMajorVersion());
            out.println("minorVersion = " + context.getMinorVersion());
            out.println("serverInfo = " + context.getServerInfo());
            out.println("servletContextName = " + context.getServletContextName());

            out.leave();
        }

        resp.getWriter().print(out.toString());
        return null;
    }

    static void dumpProperties(Class<?> type, String var) {
        Method[] methods = type.getMethods();
        Arrays.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (Method method : methods) {
            String name = method.getName();
            if (!name.startsWith("get"))
                continue;

            if (method.getParameterTypes().length != 0)
                continue;

            String propName = Strings.lcfirst(name.substring(3));
            System.out.println("out.println(\"" + propName + " = \" + " + var + "." + name + "()); ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        dumpProperties(HttpServletRequest.class, "req");
        dumpProperties(HttpServletResponse.class, "resp");
        dumpProperties(HttpSession.class, "session");
        dumpProperties(ServletContext.class, "ctx");
        dumpProperties(Cookie.class, "cookie");
    }

}
