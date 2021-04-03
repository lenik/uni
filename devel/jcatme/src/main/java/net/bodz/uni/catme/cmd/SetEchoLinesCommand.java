package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class SetEchoLinesCommand extends AbstractCommand {

    int value;

    public SetEchoLinesCommand(int value) {
        this.value = value;
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        frame.setEchoLines(value);
    }

    public static final SetEchoLinesCommand ECHO = new SetEchoLinesCommand(1);
    public static final SetEchoLinesCommand NOECHO = new SetEchoLinesCommand(0);

}
