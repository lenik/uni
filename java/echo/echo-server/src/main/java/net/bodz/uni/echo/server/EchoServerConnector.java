package net.bodz.uni.echo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class EchoServerConnector
        extends ServerConnector {

    static final Logger logger = LoggerFactory.getLogger(EchoServerConnector.class);

    public EchoServerConnector(Server server) {
        super(server);
    }

    public EchoServerConnector(Server server, int acceptors, int selectors) {
        super(server, acceptors, selectors);
    }

    public EchoServerConnector(Server server, ConnectionFactory... factories) {
        super(server, factories);
    }

    public EchoServerConnector(Server server, int acceptors, int selectors, ConnectionFactory... factories) {
        super(server, acceptors, selectors, factories);
    }

    @Override
    public void open()
            throws IOException {
        boolean already = getTransport() != null;
        super.open();

        if (!already) {
            ConnectionFactory factory = getDefaultConnectionFactory();
            String protocol = factory.getProtocol();

            ServerSocketChannel ch = (ServerSocketChannel) getTransport();
            ServerSocket socket = ch.socket();
            int port = socket.getLocalPort();

            logger.info("Server connector of " + protocol + " at port " + port);
        }
    }

}
