package net.bodz.uni.echo.config;

import javax.servlet.ServletRequestEvent;

public abstract class AbstractServletRequestListener
        implements IServletRequestListener {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    }

}
