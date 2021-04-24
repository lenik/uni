package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class InitJsCommand
        extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(InitJsCommand.class);

    public InitJsCommand() {
        super();
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        frame.getParser().initScriptContext();
    }

    public static final InitJsCommand INSTANCE = new InitJsCommand();

}
