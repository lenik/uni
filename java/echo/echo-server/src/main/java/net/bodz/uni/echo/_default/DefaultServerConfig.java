package net.bodz.uni.echo._default;

import java.util.ServiceLoader;

import net.bodz.bas.c.javax.servlet.http.RequestLogger;
import net.bodz.bas.http.config.IServletContextConfigurer;
import net.bodz.bas.http.config.ServletContextConfig;
import net.bodz.bas.http.config.ServletDescriptor;
import net.bodz.bas.http.ctx.CurrentHttpService;

public class DefaultServerConfig
        extends ServletContextConfig {

    public static final String ATTRIBUTE_PORT = "echo.port";

    String[] hintFilenames = { "WEB-INF/web.xml", "index.html" };

    public DefaultServerConfig() {
        ServiceLoader<IServletContextConfigurer> configurers = ServiceLoader.load(IServletContextConfigurer.class);

        int portNumber = 8080;
        String echoPort = System.getProperty(ATTRIBUTE_PORT);
        if (echoPort != null)
            portNumber = Integer.parseInt(echoPort);
        setPortNumber(portNumber);

        addWelcomeFile("index.html");
        addWelcomeFile("index.htm");

        addServletRequestListener(new CurrentHttpService());
        addServletRequestListener(new RequestLogger());

        /** Workaround: There is no HttpResponse parameter in servlet-request-event. */
        // addFilter(CurrentHttpService.class, "/*");
        for (IServletContextConfigurer configurer : configurers)
            configurer.filters(this);

        for (IServletContextConfigurer configurer : configurers)
            configurer.servlets(this);

        ServletDescriptor unionServlet = addServlet(EchoResourceServlet.class, "/");
        unionServlet.setPriority(PRIORITY_FALLBACK);
        unionServlet.setInitParam("welcomeServlets", "true");
    }

}
