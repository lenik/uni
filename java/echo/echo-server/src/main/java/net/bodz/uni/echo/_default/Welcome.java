package net.bodz.uni.echo._default;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.bodz.bas.c.javax.servlet.http.AbstractHttpFilter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

/**
 * Redirect start URL (/) to welcome page.
 */
public class Welcome
        extends AbstractHttpFilter {

    static Logger logger = LoggerFactory.getLogger(Welcome.class);

    boolean displayed;

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public String getPreferredMapping() {
        return "/";
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!displayed) {
            HttpServletResponse resp = response;

            resp.sendRedirect("logo/index.html");

            displayed = true;
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
