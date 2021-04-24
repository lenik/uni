package net.bodz.uni.catme.cmd;

import java.io.IOException;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.IFrame;

public class TemplateCommand
        extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(TemplateCommand.class);

    boolean qName;
    InclusionCommand inclusion;

    public TemplateCommand(boolean qName) {
        super(SCALAR, LIST);
        this.qName = qName;
        this.inclusion = qName ? InclusionCommand.MIXIN : InclusionCommand.INCLUDE;
    }

    @Override
    public void prepare(IFrame frame, CommandOptions options, Object... args) {
        if (frame == null)
            throw new NullPointerException("frame");
        frame.setEchoLines(1);
        frame.setSkipLines(-1);
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... _args)
            throws IOException, ParseException {
        if (frame == null)
            throw new NullPointerException("frame");
        inclusion.execute(frame, options, _args);
    }

    public static final TemplateCommand TEMPLATE_FILE = new TemplateCommand(false);
    public static final TemplateCommand TEMPLATE = new TemplateCommand(true);

}
