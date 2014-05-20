package net.bodz.uni.site;

import java.io.File;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.err.IllegalConfigException;
import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.web.servlet.ClassResourceAccessorServlet;
import net.bodz.bas.web.servlet.FileAccessorServlet;
import net.bodz.uni.echo._default.DefaultServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;

public class UniSiteServerConfig
        extends DefaultServerConfig {

    public UniSiteServerConfig() {
        ServletDescriptor webjarsLink = addServlet(ClassResourceAccessorServlet.class, "/webjars/*");
        webjarsLink.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "META-INF/resources/webjars");

        ServletDescriptor fontsLink = addServlet(FileAccessorServlet.class, "/fonts/*");
        fontsLink.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/usr/share/fonts");

        ServletDescriptor javascriptLink = addServlet(FileAccessorServlet.class, "/js/*");
        javascriptLink.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/usr/share/javascript");

        ServletDescriptor imgLink = addServlet(FileAccessorServlet.class, "/img/*");
        imgLink.setInitParam(FileAccessorServlet.ATTRIBUTE_PATH, //
                "/mnt/istore/projects/design/img");

        PathDispatchServlet.startObject = new UniSite(getBaseDirFromSrc());
        addServlet(PathDispatchServlet.class, "/*");
    }

    public static File getBaseDirFromSrc() {
        File pomDir = MavenPomDir.fromClass(UniSite.class).getBaseDir();
        File baseDir = pomDir.getParentFile().getParentFile();
        if (baseDir == null || !baseDir.exists())
            throw new IllegalConfigException("Can't find base dir of the uni project.");
        return baseDir;
    }

}
