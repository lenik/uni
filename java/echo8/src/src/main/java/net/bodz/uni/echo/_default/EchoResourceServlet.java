package net.bodz.uni.echo._default;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet_welcome_patch;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.io.resource.builtin.OutputStreamTarget;
import net.bodz.bas.io.resource.builtin.URLResource;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.io.resource.tools.StreamWriting;
import net.bodz.bas.std.rfc.mime.ContentType;
import net.bodz.uni.echo.resource.IResourceProvider;
import net.bodz.uni.echo.server.EchoServletContextHandler;

/**
 * This servlet should be served as the default servlet, which means to replace the default one.
 */
public class EchoResourceServlet
        extends DefaultServlet_welcome_patch {

    private static final long serialVersionUID = 1L;

    static Logger logger = Log.getLogger(EchoResourceServlet.class);

    boolean generateIndex;
    IResourceProvider resourceProvider;

    public EchoResourceServlet() {
        generateIndex = false; // TODO configurable.
    }

    /**
     * @see EchoServletContextHandler#getResource(String)
     * @see OverlappedBases#searchResource(String)
     */
    @Override
    public Resource getResource(String pathInContext) {
        logger.debug("Get overlapped resource: " + pathInContext);

        URL resourceUrl = resourceProvider.getResource(pathInContext);

        // Not in search-bases, fallback to the default one (which is resource-base based).
        if (resourceUrl == null) {
            // .jsf => .xhtml
            if (pathInContext.endsWith(".jsf")) {
                String xhtmlPath = pathInContext.substring(0, pathInContext.length() - 4) + ".xhtml";
                URL xhtmlUrl = resourceProvider.getResource(xhtmlPath);
                if (xhtmlUrl != null) {
                    String _url = xhtmlUrl.toString();
                    _url = _url.substring(0, _url.length() - 6) + ".jsf";
                    try {
                        resourceUrl = new URL(_url);
                    } catch (MalformedURLException e) {
                        throw new UnexpectedException("URL subst should work for: " + _url, e);
                    }
                }
            }

            if (resourceUrl == null)
                return null;
            else {
                logger.debug("Resolved as servlet path: " + resourceUrl);
            }
        }

        Resource resource;
        try {
            resource = Resource.newResource(resourceUrl);
            // logger.debug("    => " + resource);
        } catch (IOException e) {
            logger.ignore(e);
            return null;
        }

        return resource;
    }

    /**
     * Local HTTP-Get Implementation. (Not used)
     */
    // @Override
    protected void _doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        logger.debug("Get overlapped resource: " + path);

        if (path.startsWith("/"))
            path = path.substring(1);

        URL resourceUrl = resourceProvider.getResource(path);

        if (resourceUrl == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        URLResource resource = new URLResource(resourceUrl);

        if (resourceUrl.getPath().endsWith("/")) {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("utf-8");

            String dirName = req.getRequestURI();

            PrintWriter out = resp.getWriter();
            out.println("<html>");
            out.println("<head><title>" + "Index of " + dirName + "</title></head>");
            out.println("<body>");
            out.println("<h1>" + "Index of " + dirName + "</h1>");
            out.println("<hr />");

            for (String baseName : resource.tooling()._for(StreamReading.class).listLines()) {
                baseName = baseName.trim();
                if (baseName.isEmpty())
                    continue;

                out.print("<div>");
                out.print("<a href='" + baseName + "'>" + baseName + "</a>");
                out.println("</div>");
            }

            out.println("</body>");
            out.println("</html>");
            return;
        }

        String extension = FilePath.getExtension(path);
        ContentType contentType = ContentType.forExtension(extension);

        if (contentType != null)
            resp.setContentType(contentType.getName());

        // Also guess the encoding?

        ServletOutputStream out = resp.getOutputStream();
        new OutputStreamTarget(out).tooling()._for(StreamWriting.class).writeBytes(resource);
    }

    static URL searchClassInherited(Class<?> clazz, String path) {
        while (clazz != null) {

            URL resource = clazz.getResource(path);
            if (resource != null)
                return resource;

            clazz = clazz.getSuperclass();
        }

        return null;
    }

}
