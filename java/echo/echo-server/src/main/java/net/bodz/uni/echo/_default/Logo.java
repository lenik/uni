package net.bodz.uni.echo._default;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.bodz.bas.c.string.StringPart;
import net.bodz.bas.io.res.builtin.OutputStreamTarget;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class Logo
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static Logger logger = LoggerFactory.getLogger(Logo.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // logger.debug("Logo servlet: " + req.getRequestURI());

        String base = StringPart.afterLast(req.getRequestURI(), '/');

        URL url = getClass().getResource("logo/" + base);
        if (url == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        URLResource resource = new URLResource(url);

        ServletOutputStream out = resp.getOutputStream();
        new OutputStreamTarget(out).tooling()._for(StreamWriting.class).write(resource);

        out.close();
    }

}
