package net.bodz.uni.echo.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.bodz.bas.util.order.IPriority;

public interface IServletContextListener
        extends ServletContextListener, IPriority {

    boolean isIncluded(ServletContextEvent event);

}
