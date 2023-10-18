package net.bodz.uni.echo._default;

import java.io.IOException;
import java.net.URL;

import net.bodz.bas.c.string.StringPart;
import net.bodz.bas.io.res.builtin.OutputStreamTarget;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.servlet.HttpServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        new OutputStreamTarget(out).to(StreamWriting.class).write(resource);

        out.close();
    }

}
