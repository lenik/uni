package net.bodz.uni.echo.config;

import javax.servlet.http.HttpSessionEvent;

public abstract class AbstractHttpSessionActivationListener
        implements IHttpSessionActivationListener {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent se) {
    }

}
