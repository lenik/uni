package net.bodz.uni.echo.client;

import java.net.URL;

import net.bodz.bas.http.config.ServletContextConfig;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class PrintCommand
        extends AbstractEchoClientCommand {

    static final Logger logger = LoggerFactory.getLogger(PrintCommand.class);

    EchoClient client;
    ServletContextConfig config;
    IPrintOut out;

    public PrintCommand(EchoClient client, IPrintOut out) {
        this.client = client;
        this.out = out;
    }

    @Override
    public void execute(String... args)
            throws Exception {
        if (args.length == 0) {
            logger.error("No variable name specified.");
            return;
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
