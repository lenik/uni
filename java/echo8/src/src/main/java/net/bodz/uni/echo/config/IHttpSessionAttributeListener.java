package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionAttributeListener;

import net.bodz.bas.util.order.IPriority;

public interface IHttpSessionAttributeListener
        extends HttpSessionAttributeListener, IPriority {

}
