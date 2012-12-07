package net.bodz.bas.c.java.net;

import java.io.Serializable;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.javax.servlet.http.ThreadServletContext;
import net.bodz.bas.err.UnexpectedException;

public class Href
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private final HrefMode mode;
    private final String href;

    public Href(String href) {
        if (href == null)
            throw new NullPointerException("url");
        this.href = href;
        if (href.contains("://"))
            mode = HrefMode.URL;
        else if (href.startsWith("/"))
            mode = HrefMode.ABSOLUTE;
        else
            mode = HrefMode.RELATIVE;
    }

    public Href(URL url) {
        this.href = url.toString();
        this.mode = HrefMode.URL;
    }

    public Href(Href context, String... specs) {
        this(join(context.href, specs));
    }

    protected HttpServletRequest getRequest() {
        HttpServletRequest request = ThreadServletContext.getRequestOpt();
        return request;
    }

    /**
     * file = path [?query...] [#id]
     */
    public String getRelativeFile() {
        if (mode == HrefMode.RELATIVE)
            return href;

        HttpServletRequest request = getRequest();
        if (request == null)
            throw new NullPointerException("request");

        switch (mode) {
        case URL:
            String requestURL = request.getRequestURL().toString();
            String hrefPrefix = getHostPrefix(href);
            String reqPrefix = getHostPrefix(requestURL);
            if (!hrefPrefix.equals(reqPrefix))
                return href;

            String path = href.substring(hrefPrefix.length());
            if (path.isEmpty())
                path = "/";
            String refPath = requestURL.substring(reqPrefix.length());
            if (refPath.isEmpty())
                refPath = "/";
            return FilePath.getRelativePath(path, refPath);

        case ABSOLUTE:
            String requestURI = request.getRequestURI();
            return FilePath.getRelativePath(href, requestURI);

        default:
            throw new UnexpectedException("Bad href mode: " + mode);
        }
    }

    public String getRelativePath() {
        String path = getRelativeFile();

        int ques = path.lastIndexOf('?');
        if (ques != -1)
            path = path.substring(0, ques);

        int sharp = path.lastIndexOf('#');
        if (sharp != -1)
            path = path.substring(0, sharp);

        return path;
    }

    @Override
    public String toString() {
        HttpServletRequest request = getRequest();
        if (request == null)
            return href;
        else
            return getRelativeFile();
    }

    public static String getHostPrefix(String url) {
        int hostLen = url.indexOf("://");
        if (hostLen == -1)
            return null;

        hostLen = url.indexOf('/', hostLen + 3);
        if (hostLen == -1)
            return url;
        else
            return url.substring(0, hostLen);
    }

    public static String join(String context, String... specs) {
        if (specs.length == 1)
            return join(context, specs[0]);

        for (int i = 0; i < specs.length; i++)
            context = join(context, specs[i]);
        return context;
    }

    public static String join(String context, String spec) {
        if (spec.contains("://"))
            return spec;
        if (spec.startsWith("/"))
            return spec;

        int lastSlash = context.lastIndexOf('/');
        if (lastSlash != -1)
            return spec;

        context = context.substring(0, lastSlash + 1);
        return context + spec;
    }

    public static String getRelativePathTo(String href, String ref) {
        String relativeName = FilePath.getRelativePath(href, ref, '/');
        return relativeName;
    }

}
