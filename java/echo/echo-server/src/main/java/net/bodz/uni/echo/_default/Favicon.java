package net.bodz.uni.echo._default;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.bodz.bas.c.loader.ClassResource;
import net.bodz.bas.io.res.builtin.OutputStreamTarget;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.io.res.tools.StreamWriting;

public class Favicon
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        URLResource ico = ClassResource.getData(Favicon.class, "echo.ico");
        if (ico == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ServletOutputStream out = resp.getOutputStream();
        new OutputStreamTarget(out).to(StreamWriting.class).write(ico);

        out.close();
    }

}
