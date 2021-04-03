package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class SetSkipLinesCommand extends AbstractCommand {

    int value;

    public SetSkipLinesCommand(int value) {
        this.value = value;
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        frame.setSkipLines(value);
        if (value == 0)
            frame.setSkipToPattern(null);
    }

    public static final SetSkipLinesCommand SKIP = new SetSkipLinesCommand(1);
    public static final SetSkipLinesCommand NOSKIP = new SetSkipLinesCommand(0);

}
