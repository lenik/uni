package net.bodz.uni.echo.test;

import java.util.Random;

import net.bodz.uni.echo._default.DefaultServerConfig;

public class TestServerConfig
        extends DefaultServerConfig {

    public TestServerConfig() {
        int portNumber = getPortNumber();
        portNumber = 0;
        setPortNumber(portNumber);

        int rand = new Random().nextInt(10000);
        String contextPath = "/app" + rand;
        setContextPath(contextPath);
    }

}
