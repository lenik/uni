package net.bodz.uni.echo.server;

import java.net.URL;

import net.bodz.bas.c.string.StringPart;
import net.bodz.bas.err.IllegalUsageException;

public class EchoServerUtils {

    /**
     * setResourceBase only affects DefaultServlet.
     *
     * [However, it's used to get WEB-INF/web.xml, too.]
     *
     * NOTE: Using RabbitServer to override the resource resolver.
     */
    // getServletContextHandler().setResourceBase(resourceRoot);
    public static String searchResourceRoot(Class<?> baseClass, String... hintFilenames) {
        String resourceRoot = null;

        Class<?> chain = baseClass;
        while (chain != null) {
            resourceRoot = _searchResourceRoot(chain);

            if (resourceRoot != null)
                break;

            chain = chain.getSuperclass();
        }

        if (resourceRoot == null)
            throw new IllegalUsageException("Can't find resource root for " + baseClass);

        return resourceRoot;
    }

    static String _searchResourceRoot(Class<?> clazz, String... hintFilenames) {
        URL hintResourceURL = null;

        for (String hintFilename : hintFilenames) {

            hintResourceURL = clazz.getResource(hintFilename);

            if (hintResourceURL != null) {
                String hintResourcePath = hintResourceURL.toString();

                int shrink = hintResourcePath.length() - hintFilename.length();
                String hintResourceRoot = hintResourcePath.substring(0, shrink);

                // remove the trailing /*.
                hintResourceRoot = StringPart.beforeLast(hintResourceRoot, '/');
                return hintResourceRoot;
            }
        }

        return null;
    }

}
