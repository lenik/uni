package net.bodz.uni.snmcc;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * s.n.m. C-framework Console
 */
@MainVersion({ 0, 1 })
@ProgramName("snmcc")
@RcsKeywords(id = "$Id$")
public class SnmCC
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
    }

    public static void main(String[] args)
            throws Exception {
        new SnmCC().execute(args);
    }

}
