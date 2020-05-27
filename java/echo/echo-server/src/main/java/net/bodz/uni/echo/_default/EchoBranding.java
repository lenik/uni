package net.bodz.uni.echo._default;

import net.bodz.bas.servlet.config.AbstractServletContextConfigurer;
import net.bodz.bas.servlet.config.ServletContextConfig;

public class EchoBranding
        extends AbstractServletContextConfigurer {

    @Override
    public void filters(ServletContextConfig config) {
        // addFilter(Welcome.class, "/");
    }

    @Override
    public void servlets(ServletContextConfig config) {
        config.addServlet(Favicon.class, "/favicon.ico");

        // The wildcard * is needed, cuz they are class resources, not overlapped resources.
        config.addServlet(Logo.class, "/logo/*");
    }

}
