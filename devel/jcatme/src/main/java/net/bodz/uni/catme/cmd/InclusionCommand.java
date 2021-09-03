package net.bodz.uni.catme.cmd;

import java.io.IOException;
import java.util.List;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.script.io.ResourceVariant;
import net.bodz.uni.catme.CommandOptions;
import net.bodz.uni.catme.FileFrame;
import net.bodz.uni.catme.FrameFn;
import net.bodz.uni.catme.IFrame;

public class InclusionCommand
        extends AbstractCommand {

    static final Logger logger = LoggerFactory.getLogger(InclusionCommand.class);

    boolean once;
    boolean qName;
    boolean silent;

    public InclusionCommand(boolean once, boolean qName) {
        this(once, qName, false);
    }

    public InclusionCommand(boolean once, boolean qName, boolean silent) {
        super(SCALAR, LIST);
        this.once = once;
        this.qName = qName;
        this.silent = silent;
    }

    @Override
    public void execute(IFrame frame, CommandOptions options, Object... _args)
            throws IOException, ParseException {
        if (frame == null)
            throw new NullPointerException("frame");
        if (_args.length < 1)
            throw new IllegalArgumentException("expect path or name to be included.");
        String path = _args[0].toString();

        if (once) {
            if (!frame.addImported(path)) {
                logger.trace("already imported: " + path);
                return;
            }
        }

        ResourceVariant resource;
        if (qName) {
            resource = frame.resolveQName(path);
            if (resource == null) {
                error(frame, "Can't resolve qName: " + path);
                return;
            }
        } else {
            resource = frame.resolveHref(path);
            if (resource == null) {
                error(frame, "Can't resolve href: " + path);
                return;
            }
        }

        FileFrame ff = frame.getClosestFileFrame();
        FileFrame childFrame = ff.createChildFrame(resource);

        if (_args.length >= 2) {
            @SuppressWarnings("unchecked")
            List<String> args = (List<String>) _args[1];
            int argN = args.size();
            int argIndex = 0;
            for (int i = 0; i < argN; i++) {
                String arg = args.get(i);
                int eq = arg.indexOf('=');
                if (eq != -1) {
                    String name = arg.substring(0, eq);
                    String val = arg.substring(eq + 1);
                    childFrame.putVar(name, val);
                } else {
                    argIndex++;
                    String name = String.valueOf(argIndex);
                    childFrame.putVar(name, arg);
                }
            }
        }

        childFrame.parse();
    }

    void error(IFrame frame, String message) {
        if (silent)
            return;
        logger.error(message);
        System.err.print(FrameFn.dump(frame));
    }

    public static final InclusionCommand INCLUDE = new InclusionCommand(false, false);
    public static final InclusionCommand INCLUDE_ONCE = new InclusionCommand(true, false);
    public static final InclusionCommand SINCLUDE = new InclusionCommand(false, false, true);
    public static final InclusionCommand MIXIN = new InclusionCommand(false, true);
    public static final InclusionCommand IMPORT = new InclusionCommand(true, true);

}
