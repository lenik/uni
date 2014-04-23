package net.bodz.uni.site;

import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.test.AbstractWebAppTester;

public class UniSiteTester
        extends AbstractWebAppTester {

    @Override
    protected EchoServerConfig createConfig() {
        return new UniSiteServerConfig();
    }

    public static void main(String[] args)
            throws Exception {
        new UniSiteTester().makeClient().go("");
    }

}
