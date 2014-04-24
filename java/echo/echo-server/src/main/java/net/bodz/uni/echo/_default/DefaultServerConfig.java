package net.bodz.uni.echo._default;

import net.bodz.bas.c.javax.servlet.http.RequestLogger;
import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.bas.http.ctx.CurrentServletContext;
import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;

public class DefaultServerConfig
        extends EchoServerConfig {

    public static final String ATTRIBUTE_PORT = "echo.port";

    String[] hintFilenames = { "WEB-INF/web.xml", "index.html" };

    public DefaultServerConfig() {
        int portNumber = 8080;
        String echoPort = System.getProperty(ATTRIBUTE_PORT);
        if (echoPort != null)
            portNumber = Integer.parseInt(echoPort);
        setPortNumber(portNumber);

        addWelcomeFile("index.html");
        addWelcomeFile("index.htm");

        addServletContextListener(new CurrentServletContext());
        addServletRequestListener(new CurrentHttpService());
        addServletRequestListener(new RequestLogger());

        /** Workaround: There is no HttpResponse parameter in servlet-request-event. */
        // addFilter(CurrentHttpService.class, "/*");

        // addFilter(Welcome.class, "/");

        addServlet(Favicon.class, "/favicon.ico");

        // The wildcard * is needed, cuz they are class resources, not overlapped resources.
        addServlet(Logo.class, "/logo/*");

        ServletDescriptor unionServlet = addServlet(EchoResourceServlet.class, "/");
        unionServlet.setPriority(PRIORITY_FALLBACK);
        unionServlet.setInitParam("welcomeServlets", "true");
    }

}
