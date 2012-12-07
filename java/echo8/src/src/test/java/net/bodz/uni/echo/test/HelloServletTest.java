package net.bodz.uni.echo.test;

import org.junit.Test;

import net.bodz.uni.echo._default.HelloServlet;
import net.bodz.uni.echo.test.EchoTestApp;

public class HelloServletTest
        extends EchoTestApp {

    @Test
    public void testHello()
            throws Exception {
        config.addServlet(HelloServlet.class, "/hello");
        server.start();
        String content = client.httpGet("/hello?name=foo").getContent();
        assertEquals("hello, foo\n", content);
    }

}
