package net.bodz.uni.fmt.cfb;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.program.meta.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

@MainVersion({ 0, 0 })
@ProgramName("cfb2t")
public class Cfb2t
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
    }

    public static void main(String[] args)
            throws Exception {
        new Cfb2t().execute(args);
    }

}
