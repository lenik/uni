package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionActivationListener;

import net.bodz.bas.t.order.IPriority;

public interface IHttpSessionActivationListener
        extends HttpSessionActivationListener, IPriority {

}
