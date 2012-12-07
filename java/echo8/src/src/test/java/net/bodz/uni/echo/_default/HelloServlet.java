package net.bodz.uni.echo._default;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String hack = req.getParameter("hack");

        PrintWriter out = resp.getWriter();
        if (hack != null) {
            out.println("hey, hacker " + name);
        } else {
            out.println("hello, " + name);
        }
    }

}