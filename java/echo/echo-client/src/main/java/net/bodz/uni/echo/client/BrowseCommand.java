package net.bodz.uni.echo.client;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.bodz.bas.c.system.SystemProperties;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.echo.config.EchoServerConfig;

public class BrowseCommand
        extends AbstractEchoClientCommand {

    static final Logger logger = LoggerFactory.getLogger(BrowseCommand.class);

    EchoClient client;
    EchoServerConfig config;
    String systemBrowser;

    public BrowseCommand(EchoClient client, EchoServerConfig config) {
        if (client == null)
            throw new NullPointerException("client");
        if (config == null)
            throw new NullPointerException("config");
        this.client = client;
        this.config = config;
    }

    String findSystemBrowser() {
        String[] browsers = { "mate-open", "gnome-open", "google-chrome", "firefox", "iexplore" };
        String browser = null;
        b: for (String br : browsers) {
            for (String dir : System.getenv("PATH").split(SystemProperties.getPathSeparator())) {
                if (new File(dir, br).canExecute()) {
                    browser = br;
                    break b;
                }
            }
        }
        return browser;
    }

    @Override
    public void execute(String... args)
            throws Exception {
        String location;

        if (args.length == 0)
            location = client.location;
        else
            location = args[0];

        URL url = config.toURL(location);
        logger.info("Browse: " + url);

        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        try {
            Desktop.getDesktop().browse(uri);
        } catch (UnsupportedOperationException e) {
            if (systemBrowser == null) {
                systemBrowser = findSystemBrowser();
                if (systemBrowser == null)
                    throw new RuntimeException("No available browser.");
            }
            logger.debug("Launch browser " + systemBrowser + " for " + uri);

            Runtime.getRuntime().exec(new String[] { systemBrowser, uri.toString() }, null);
        }
    }

}
