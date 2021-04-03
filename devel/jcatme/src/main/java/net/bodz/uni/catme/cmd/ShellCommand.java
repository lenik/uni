package net.bodz.uni.catme.cmd;

import java.io.IOException;
import java.util.List;

import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class ShellCommand extends AbstractCommand {

    public ShellCommand() {
        super(LIST);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        if (args.length < 1)
            throw new IllegalArgumentException("expect cmdarray.");
        @SuppressWarnings("unchecked")
        List<String> cmdarray = (List<String>) args[0];
        String[] cmdv = cmdarray.toArray(new String[0]);
        try {
            frame.getParser().shell(cmdv);
        } catch (InterruptedException e) {
            ; // just ignore
        }
    }

    public static final ShellCommand INSTANCE = new ShellCommand();

}
