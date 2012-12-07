package net.bodz.uni.echo._default;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class Welcome
        implements Filter {

    static Logger logger = LoggerFactory.getLogger(Welcome.class);

    boolean displayed;

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!displayed) {
            HttpServletResponse resp = (HttpServletResponse) response;

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
