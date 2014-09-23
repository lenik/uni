package net.bodz.uni.site;

import java.io.File;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.err.IllegalConfigException;
import net.bodz.bas.html.servlet.PathDispatchServlet;
import net.bodz.bas.http.ctx.CurrentRequestContextTeller;
import net.bodz.bas.http.servlet.ClassResourceAccessorServlet;
import net.bodz.bas.http.servlet.FileAccessorServlet;
import net.bodz.bas.i18n.LocaleCtl;
import net.bodz.uni.echo._default.DefaultServerConfig;
import net.bodz.uni.echo.config.ServletDescriptor;

public class UniSiteServerConfig
        extends DefaultServerConfig {

    public UniSiteServerConfig() {
        configEnv();
        configServlets();
    }

    void configEnv() {
        LocaleCtl localeCtl = LocaleCtl.LOCALE;
        localeCtl.setTeller(new CurrentRequestContextTeller());
    }

    void configServlets() {
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

        PathDispatchServlet.startObject = new UniSite(getUniDirFromSrc());
        addServlet(PathDispatchServlet.class, "/*");
    }

    public static File getUniDirFromSrc() {
        File pomDir = MavenPomDir.fromClass(UniSite.class).getBaseDir();
        File javaDir = pomDir.getParentFile();
        File uniDir = javaDir.getParentFile();
        if (uniDir == null || !uniDir.exists())
            throw new IllegalConfigException("Can't find base dir of the uni project.");
        return uniDir;
    }

}
