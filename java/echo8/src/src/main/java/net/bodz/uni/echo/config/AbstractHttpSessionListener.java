package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionEvent;

public abstract class AbstractHttpSessionListener
        implements IHttpSessionListener {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    }

}
