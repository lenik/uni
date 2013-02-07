package net.bodz.uni.echo.test;

import java.net.URL;

import net.bodz.uni.echo._default.HelloServlet;

import org.junit.Test;

public class EchoTestAppTest
        extends EchoTestApp {

    @Test
    public void testGetURL()
            throws Exception {
        config.addServlet(HelloServlet.class, "/hello");
        URL url = config.toURL("hello?name=foo");
        String content = client.httpGet(url).getContent();
        assertEquals("hello, foo\n", content);
    }

    @Test
    public void testGetEmptyParam()
            throws Exception {
        config.addServlet(HelloServlet.class, "/hello");
        URL url = config.toURL("hello?hack&name=foo");
        String content = client.httpGet(url).getContent();
        assertEquals("hey, hacker foo\n", content);
    }

    public static void main(String[] args)
            throws Exception {
        new EchoTestAppTest().makeClient().go("/hello");
    }

}
