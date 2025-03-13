package net.bodz.uni.echo._default;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.bodz.bas.servlet.HttpServlet;

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