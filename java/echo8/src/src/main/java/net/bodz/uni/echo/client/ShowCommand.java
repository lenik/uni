package net.bodz.uni.echo.client;

import java.net.URL;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.uni.echo.config.EchoServerConfig;

public class ShowCommand
        extends AbstractEchoClientCommand {

    static final Logger logger = LoggerFactory.getLogger(ShowCommand.class);

    EchoClient client;
    EchoServerConfig config;
    IPrintOut out;

    public ShowCommand(EchoClient client, IPrintOut out) {
        this.client = client;
        this.out = out;
    }

    @Override
    public void execute(String... args)
            throws Exception {
        if (args.length == 0) {
            logger.error("No variable name specified.");
        }
        String var = args[0];
        switch (var) {
        case "l":
            out.println("Location: " + client.location);
            break;
        case "u":
            URL url = config.toURL(client.location);
            out.println("URL: " + url);
            break;
        }
    }

}
