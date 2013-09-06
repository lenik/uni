package net.bodz.uni.echo.client;

import java.util.Map.Entry;

import net.bodz.bas.io.IPrintOut;

public class HelpCommand
        extends AbstractEchoClientCommand {

    EchoClient client;
    IPrintOut out;

    public HelpCommand(EchoClient client, IPrintOut out) {
        if (out == null)
            throw new NullPointerException("out");
        this.client = client;
        this.out = client.out;
    }

    @Override
    public void execute(String... args)
            throws Exception {
        out.println("Shortcuts: ");
        for (Entry<String, IEchoClientCommand> cmd : client.commandMap.entrySet()) {
            String key = cmd.getKey();
            if (key.isEmpty())
                key = "(empty)";
            out.println("    " + key + ": " + cmd.getValue());
        }
    }

}
