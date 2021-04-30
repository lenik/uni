package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilterClass;

public class EndFilterCommand
        extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(EndFilterCommand.class);

    public EndFilterCommand() {
        super(SCALAR);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... _args)
            throws IOException, ParseException {
        if (frame == null)
            throw new NullPointerException("frame");
        if (_args.length < 1)
            throw new IllegalArgumentException("expect filter name.");
        String name = _args[0].toString();
        ITextFilterClass filterClass = frame.getFilter(name);
        if (filterClass == null)
            throw new ParseException("Undefined filter: " + name);

        frame.endFilter(name);
    }

    public static final EndFilterCommand END = new EndFilterCommand();

}
