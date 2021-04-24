package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class SetEchoLinesCommand
        extends AbstractCommand {

    int value = 1;

    public SetEchoLinesCommand() {
        super(SCALAR);
    }

    public SetEchoLinesCommand(int value) {
        this.value = value;
    }

    @Override
    public void prepare(IFrame frame, CommandOptions options, Object... args) {
        if (frame == null)
            throw new NullPointerException("frame");
        int value = this.value;
        if (args.length >= 1) {
            String str = (String) args[0];
            if (!StringPred.isDecimal(str))
                throw new IllegalArgumentException("Not a number: " + str);
            value = Integer.parseInt(str);
        }
        frame.setEchoLines(value);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
    }

    public static final SetEchoLinesCommand ECHO = new SetEchoLinesCommand();
    public static final SetEchoLinesCommand NOECHO = new SetEchoLinesCommand(0);

}
