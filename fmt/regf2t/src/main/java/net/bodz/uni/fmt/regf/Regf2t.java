package net.bodz.uni.fmt.regf;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

@MainVersion({ 0, 0 })
@ProgramName("regf2t")
public class Regf2t
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
    }

    public static void main(String[] args)
            throws Exception {
        new Regf2t().execute(args);
    }

}
