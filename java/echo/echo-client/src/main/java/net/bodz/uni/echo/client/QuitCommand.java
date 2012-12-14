package net.bodz.uni.echo.client;

import net.bodz.bas.err.control.ControlExit;
import net.bodz.uni.echo.server.EchoServer;

public class QuitCommand
        extends AbstractEchoClientCommand {

    EchoServer server;

    public QuitCommand(EchoServer server) {
        if (server == null)
            throw new NullPointerException("server");
        this.server = server;
    }

    @Override
    public void execute(String... args)
            throws Exception {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        throw new ControlExit();
    }

}
