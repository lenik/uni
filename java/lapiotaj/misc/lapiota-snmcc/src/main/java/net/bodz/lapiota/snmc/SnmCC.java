package net.bodz.lapiota.snmc;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.info.Doc;

/**
 * s.n.m. C-framework Console
 */
@Doc("s.n.m. C-framework Console")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class SnmCC
        extends BasicCLI {

    @Override
    protected void doMain(String[] args)
            throws Exception {
    }

    public static void main(String[] args)
            throws Exception {
        new SnmCC().run(args);
    }

}
