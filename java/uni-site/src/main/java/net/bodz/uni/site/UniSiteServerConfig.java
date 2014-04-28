package net.bodz.uni.site;

import net.bodz.bas.html.path.PathDispatchServlet;
import net.bodz.bas.web.servlet.FileAccessorServlet;
import net.bodz.uni.echo._default.DefaultServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;
import net.bodz.uni.site.view.SiteApplication;

public class UniSiteServerConfig
        extends DefaultServerConfig {

    public UniSiteServerConfig() {
        ServletDescriptor imgLink = addServlet(FileAccessorServlet.class, "/img/*");
        imgLink.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/mnt/istore/projects/design/img");

        ServletDescriptor javascriptLink = addServlet(FileAccessorServlet.class, "/js/*");
        javascriptLink.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/usr/share/javascript");

        PathDispatchServlet.startObject = new SiteApplication();
        addServlet(PathDispatchServlet.class, "/*");
    }

}
