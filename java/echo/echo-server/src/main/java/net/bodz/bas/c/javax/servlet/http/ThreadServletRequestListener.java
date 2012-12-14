package net.bodz.bas.c.javax.servlet.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.echo.config.AbstractServletRequestListener;

public class ThreadServletRequestListener
        extends AbstractServletRequestListener {

    static Logger logger = LoggerFactory.getLogger(ThreadServletRequestListener.class);

    public static final int PRIORITY = -1;

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        logger.debug("Thread servlet context enter: " + event);

        ServletRequest request = event.getServletRequest();

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            ThreadServletContext.setRequest((HttpServletRequest) req);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        ThreadServletContext.setRequest(null);
    }

}
