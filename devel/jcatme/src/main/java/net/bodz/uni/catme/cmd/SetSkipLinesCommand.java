package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.c.string.StringPred;
import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class SetSkipLinesCommand
        extends AbstractCommand {

    int value = 1;

    public SetSkipLinesCommand() {
        super(SCALAR);
    }

    public SetSkipLinesCommand(int value) {
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
        frame.setSkipLines(value);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
    }

    public static final SetSkipLinesCommand SKIP = new SetSkipLinesCommand();
    public static final SetSkipLinesCommand NOSKIP = new SetSkipLinesCommand(0);

}
