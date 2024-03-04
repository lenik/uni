package net.bodz.lily.tool;

import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.ProgramLauncher;

@ProgramName("tstool")
public class TsTool {

    public static void main(String[] args)
            throws Throwable {

        if (args.length < 1)
            throw new IllegalArgumentException("expected tool name.");

        ProgramLauncher.main(args);
    }

}
