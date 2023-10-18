package net.bodz.uni.echo._default;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.io.res.builtin.OutputStreamTarget;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.std.rfc.mime.ContentType;
import net.bodz.bas.std.rfc.mime.ContentTypes;
import net.bodz.uni.echo.resource.IResourceProvider;
import net.bodz.uni.echo.server.EchoServer;
import net.bodz.uni.echo.server.EchoServletContextHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This servlet should be served as the default servlet, which means to replace the default one.
 */
public class EchoResourceServlet
        extends DefaultServlet {

    private static final long serialVersionUID = 1L;

    static final Logger logger = LoggerFactory.getLogger(EchoResourceServlet.class);

    boolean generateIndex;
    IResourceProvider resourceProvider;

    public EchoResourceServlet() {
        // TODO configurable.
        generateIndex = false;
    }

    @Override
    public void init()
            throws UnavailableException {
        super.init();

        EchoServer echoServer = EchoServer.fromContext(getServletContext());
        resourceProvider = echoServer.getResourceProvider();
    }

    /**
     * @see EchoServletContextHandler#getResource(String)
     * @see OverlappedBases#searchResource(String)
     */
    @Override
    public Resource getResource(String pathInContext) {
        logger.debug("get-resource: " + pathInContext);

        URL resourceUrl;
        try {
            String resourcePath = pathInContext;
            while (resourcePath.startsWith("/"))
                resourcePath = resourcePath.substring(1);
            resourceUrl = resourceProvider.getResource(resourcePath);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

        if (resourceUrl == null)
            // Not in search-bases, fallback to the default one (which is resource-base based).
            // return super.getResource(pathInContext);
            return null;

        logger.debug("Resolved as servlet path: " + resourceUrl);

        Resource resource;
        resource = Resource.newResource(resourceUrl);
        // logger.debug(" => " + resource);
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

            for (String baseName : resource.to(StreamReading.class).readLines()) {
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

        ContentType contentType = ContentTypes.application_octet_stream; // null;
        String extension = FilePath.getExtension(path);
        if (extension != null)
            contentType = ContentType.forExtension(extension);

        if (contentType != null)
            resp.setContentType(contentType.getName());

        // Also guess the encoding?

        ServletOutputStream out = resp.getOutputStream();
        new OutputStreamTarget(out).to(StreamWriting.class).write(resource);
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
