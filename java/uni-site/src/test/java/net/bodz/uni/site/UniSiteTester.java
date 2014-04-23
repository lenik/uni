package net.bodz.uni.site;

import net.bodz.bas.repr.path.PathDispatchServlet;
import net.bodz.bas.web.servlet.FileAccessorServlet;
import net.bodz.uni.echo.config.EchoServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;
import net.bodz.uni.echo.test.AbstractWebAppTester;

public class UniSiteTester
        extends AbstractWebAppTester {

    @Override
    protected EchoServerConfig createConfig() {
        EchoServerConfig config = super.createConfig();

        ServletDescriptor fileAccessorDescriptor = config.addServlet(FileAccessorServlet.class, "/img/*");
        fileAccessorDescriptor.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/mnt/istore/projects/design/img");

        PathDispatchServlet.startObject = new UniSite();
        config.addServlet(PathDispatchServlet.class, "/*");

        return config;
    }

    public static void main(String[] args)
            throws Exception {
        new UniSiteTester().makeClient().go("img/hbar/angel-city.png");
    }

}
