package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionListener;

import net.bodz.bas.util.order.IPriority;

/**
 * NOTICE: Jetty-6 doesn't support http session listener.
 *
 * @see HttpSessionListener
 */
public interface IHttpSessionListener
        extends HttpSessionListener, IPriority {

}
