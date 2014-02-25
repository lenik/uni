package net.bodz.uni.echo.test;

import java.net.URL;

import org.junit.Test;

import net.bodz.uni.echo._default.HelloServlet;
import net.bodz.uni.echo.config.EchoServerConfig;

public class EchoTestAppTest
        extends EchoTestApp {

    @Override
    protected EchoServerConfig createConfig() {
        EchoServerConfig config = super.createConfig();
        config.addServlet(HelloServlet.class, "/hello");
        return config;
    }

    @Test
    public void testGetURL()
            throws Exception {
        URL url = config.toURL("hello?name=foo");
        String content = client.httpGet(url).getContent();
        assertEquals("hello, foo\n", content);
    }

    @Test
    public void testGetEmptyParam()
            throws Exception {
        URL url = config.toURL("hello?hack&name=foo");
        String content = client.httpGet(url).getContent();
        assertEquals("hey, hacker foo\n", content);
    }

    public static void main(String[] args)
            throws Exception {
        new EchoTestAppTest().makeClient().go("hello");
    }

}
