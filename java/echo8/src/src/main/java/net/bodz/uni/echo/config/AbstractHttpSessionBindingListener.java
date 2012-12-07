package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionBindingEvent;

public abstract class AbstractHttpSessionBindingListener
        implements IHttpSessionBindingListener {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
    }

}
