package net.bodz.uni.catme.builtin;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class EvalCommand extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(EvalCommand.class);

    public EvalCommand() {
        super(CAPTURE);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        String code = (String) args[0];
        try {
            frame.getParser().eval(code);
        } catch (EvalException e) {
            logger.error("eval failed: " + e.getMessage(), e);
        }
    }

    public static final EvalCommand INSTANCE = new EvalCommand();

}
