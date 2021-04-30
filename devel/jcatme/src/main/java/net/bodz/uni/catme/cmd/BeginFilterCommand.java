package net.bodz.uni.catme.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilterClass;

public class BeginFilterCommand
        extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(BeginFilterCommand.class);

    public BeginFilterCommand() {
        super(SCALAR, LIST);
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

        List<String> args = new ArrayList<>();
        for (Object arg : _args)
            args.add((String) arg);

        frame.beginFilter(name, args);
    }

    public static final BeginFilterCommand BEGIN = new BeginFilterCommand();

}
