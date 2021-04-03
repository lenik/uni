package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.ILogSink;
import net.bodz.bas.log.LogLevel;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class LogCommand extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(LogCommand.class);

    ILogSink logSink;

    public LogCommand(LogLevel level) {
        super(CAPTURE);
        logSink = logger.get(level);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... args)
            throws IOException, ParseException {
        String message = (String) args[0];
        logSink.logMessage(message);
    }

    public static final LogCommand ERROR = new LogCommand(LogLevel.ERROR);
    public static final LogCommand WARN = new LogCommand(LogLevel.WARN);
    public static final LogCommand INFO = new LogCommand(LogLevel.INFO);
    public static final LogCommand DEBUG = new LogCommand(LogLevel.DEBUG);
    public static final LogCommand TRACE = new LogCommand(LogLevel.TRACE);

}
