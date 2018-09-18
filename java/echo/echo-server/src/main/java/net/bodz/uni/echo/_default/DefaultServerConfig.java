package net.bodz.uni.echo._default;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import net.bodz.bas.c.javax.servlet.http.RequestLogger;
import net.bodz.bas.http.HttpModFilter;
import net.bodz.bas.http.config.IServletContextConfigurer;
import net.bodz.bas.http.config.ServletContextConfig;
import net.bodz.bas.http.config.ServletDescriptor;
import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.bas.meta.codegen.IndexedTypeLoader;
import net.bodz.bas.t.iterator.Iterables;
import net.bodz.bas.t.order.PriorityComparator;

@IndexedTypeLoader(IServletContextConfigurer.class)
public class DefaultServerConfig
        extends ServletContextConfig {

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

        addServletRequestListener(new RequestLogger());
        addFilter(HttpModFilter.class, "/*");
        addFilter(CurrentHttpService.class, "/*");

        ServiceLoader<IServletContextConfigurer> configurers = ServiceLoader.load(IServletContextConfigurer.class);
        {
            List<IServletContextConfigurer> sorted = Iterables.toList(configurers);
            Collections.sort(sorted, PriorityComparator.INSTANCE);

            /** Workaround: There is no HttpResponse parameter in servlet-request-event. */
            // addFilter(CurrentHttpService.class, "/*");
            for (IServletContextConfigurer configurer : sorted)
                configurer.filters(this);

            for (IServletContextConfigurer configurer : sorted)
                configurer.servlets(this);
        }

        ServletDescriptor unionServlet = addServlet(EchoResourceServlet.class, "/");
        unionServlet.setPriority(PRIORITY_FALLBACK);
        unionServlet.setInitParam("welcomeServlets", "true");
    }

}
