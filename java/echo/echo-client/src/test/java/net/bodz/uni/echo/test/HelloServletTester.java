package net.bodz.uni.echo.test;

import org.junit.Test;

import net.bodz.uni.echo._default.HelloServlet;

public class HelloServletTester
        extends AbstractWebAppTester {

    @Test
    public void testHello()
            throws Exception {
        config.addServlet(HelloServlet.class, "/hello");
        server.start();
        String content = client.httpGet("/hello?name=foo").getContent();
        assertEquals("hello, foo\n", content);
    }

}
