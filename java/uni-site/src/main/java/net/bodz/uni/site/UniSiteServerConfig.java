package net.bodz.uni.site;

import net.bodz.bas.repr.path.PathDispatchServlet;
import net.bodz.bas.web.servlet.FileAccessorServlet;
import net.bodz.uni.echo._default.DefaultServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;

public class UniSiteServerConfig
        extends DefaultServerConfig {

    public UniSiteServerConfig() {
        ServletDescriptor fileAccessor = addServlet(FileAccessorServlet.class, "/img/*");
        fileAccessor.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/mnt/istore/projects/design/img");

        PathDispatchServlet.startObject = new UniSite();
        addServlet(PathDispatchServlet.class, "/*");
    }

}
