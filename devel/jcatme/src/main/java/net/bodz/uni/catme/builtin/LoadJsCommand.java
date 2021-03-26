package net.bodz.uni.catme.builtin;

import java.io.IOException;

import org.graalvm.polyglot.Value;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.fn.EvalException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class LoadJsCommand extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(LoadJsCommand.class);

    public LoadJsCommand() {
        super(SCALAR, LIST);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        if (args.length < 1)
            throw new IllegalArgumentException("expect module to be loaded.");
        String name = args[0].toString();

        String moduleHref = name.replace('.', '/') + ".js";

        try {
            Value loadFn = (Value) frame.getParser().eval("load");
            if (loadFn == null)
                throw new ParseException("load function isn't defined.");
            loadFn.execute(moduleHref);
        } catch (EvalException e) {
            logger.error("load-js failed: " + e.getMessage(), e);
        }
    }

    public static final LoadJsCommand INSTANCE = new LoadJsCommand();

}
