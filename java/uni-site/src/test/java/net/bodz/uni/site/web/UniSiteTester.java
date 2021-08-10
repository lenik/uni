package net.bodz.uni.site.web;

import net.bodz.bas.servlet.config.ServletContextConfig;
import net.bodz.uni.echo.test.AbstractWebAppTester;
import net.bodz.uni.site.Uni14Config;

public class UniSiteTester
        extends AbstractWebAppTester {

    @Override
    protected ServletContextConfig createConfig() {
        return new Uni14Config();
    }

    public static void main(String[] args)
            throws Exception {
        new UniSiteTester().makeClient().go("webjars/font-awesome/3.2.1/css/font-awesome.css");
    }

}
