package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionBindingListener;

import net.bodz.bas.t.order.IPriority;

public interface IHttpSessionBindingListener
        extends HttpSessionBindingListener, IPriority {

}
