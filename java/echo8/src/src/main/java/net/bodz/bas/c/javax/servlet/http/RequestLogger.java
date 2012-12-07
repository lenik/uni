package net.bodz.bas.c.javax.servlet.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class RequestLogger
        implements Filter {

    static Logger logger = LoggerFactory.getLogger(RequestLogger.class);

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        logger.debug("Request-URL: " + req.getRequestURL());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
