package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class EndTemplateCommand
        extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(EndTemplateCommand.class);

    public EndTemplateCommand() {
        super(SCALAR);
    }

    @Override
    public void prepare(IFrame frame, CommandOptions options, Object... args) {
        if (frame == null)
            throw new NullPointerException("frame");
        frame.setEchoLines(1);
        frame.setSkipLines(0);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... _args)
            throws IOException, ParseException {
    }

    public static final EndTemplateCommand END_TEMPLATE = new EndTemplateCommand();

}
