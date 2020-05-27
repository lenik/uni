package net.bodz.uni.echo._default;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.bodz.bas.io.res.builtin.OutputStreamTarget;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.bas.servlet.HttpServlet;

public class Favicon
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        URL iconUrl = Favicon.class.getResource("echo.ico");
        if (iconUrl == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream in = iconUrl.openStream();

        ServletOutputStream out = resp.getOutputStream();
        new OutputStreamTarget(out).to(StreamWriting.class).write(in);

        out.close();
    }

}
