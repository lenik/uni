package net.bodz.uni.echo._default;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.bodz.bas.io.res.builtin.OutputStreamTarget;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.bas.servlet.HttpServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
