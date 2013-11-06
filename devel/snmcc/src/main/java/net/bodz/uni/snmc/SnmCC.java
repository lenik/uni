package net.bodz.uni.snmc;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * s.n.m. C-framework Console
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
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