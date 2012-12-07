package net.bodz.uni.echo.config;

import javax.servlet.ServletRequestListener;

import net.bodz.bas.util.order.IPriority;

/**
 * @see ServletRequestListener
 * @see IServletContextListener
 */
public interface IServletRequestListener
        extends ServletRequestListener, IPriority {

}
