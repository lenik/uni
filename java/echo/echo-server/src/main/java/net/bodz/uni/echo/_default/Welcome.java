package net.bodz.uni.echo._default;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.bodz.bas.c.jakarta.servlet.http.IHttpFilter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

/**
 * Redirect start URL (/) to welcome page.
 */
public class Welcome
        implements
            IHttpFilter {

    static Logger logger = LoggerFactory.getLogger(Welcome.class);

    boolean displayed;

    @Override
    public int getPriority() {
        return 10;
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

}
