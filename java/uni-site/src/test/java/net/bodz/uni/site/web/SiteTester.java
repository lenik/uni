package net.bodz.uni.site.web;

import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.test.AbstractWebAppTester;
import net.bodz.uni.site.UniSiteServerConfig;

public class SiteTester
        extends AbstractWebAppTester {

    @Override
    protected EchoServerConfig createConfig() {
        return new UniSiteServerConfig();
    }

    public static void main(String[] args)
            throws Exception {
        new SiteTester().makeClient().go("");
    }

}
