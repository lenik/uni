package net.bodz.uni.catme.builtin;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class DnlCommand extends AbstractCommand {

    public DnlCommand() {
        super(CAPTURE);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
    }

    public static final DnlCommand INSTANCE = new DnlCommand();

}
