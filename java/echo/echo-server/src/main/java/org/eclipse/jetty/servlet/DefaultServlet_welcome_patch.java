package org.eclipse.jetty.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;

/**
 * This patch fixed the welcome file resolution problem.
 *
 * Because the default getWelcomeFile is private, so nonprivate_patch is necessary here.
 */
public class DefaultServlet_welcome_patch
        extends DefaultServlet_nonprivate_patch {

    private static final long serialVersionUID = 1L;

    @Override
    protected String getWelcomeFile(String pathInContext)
            throws MalformedURLException, IOException {
        if (_welcomes == null)
            return null;

        String welcome_servlet = null;
        for (int i = 0; i < _welcomes.length; i++) {
            String welcome_in_context = URIUtil.addPaths(pathInContext, _welcomes[i]);
            Resource welcome = getResource(welcome_in_context);
            if (welcome != null && welcome.exists())
                return _welcomes[i];

            if ((_welcomeServlets || _welcomeExactServlets) /* && welcome_servlet == null */) {

                Map.Entry<?, ?> entry = _servletHandler.getHolderEntry(welcome_in_context);

                if (entry != null && entry.getValue() != _defaultHolder
                        && (_welcomeServlets || (_welcomeExactServlets && entry.getKey().equals(welcome_in_context)))) {

                    if (welcome_servlet == null || welcome != null)
                        welcome_servlet = welcome_in_context;
                    if (welcome != null)
                        break;
                }
            }
        }
        return welcome_servlet;
    }

}
