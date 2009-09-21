package net.bodz.lapiota.snmc;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.lapiota.wrappers.BasicCLI;

/**
 * s.n.m. C-framework Console
 */
@Doc("s.n.m. C-framework Console")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class SnmCC extends BasicCLI {

    @Override
    protected void doMain(String[] args) throws Exception {
    }

    public static void main(String[] args) throws Exception {
        new SnmCC().run(args);
    }

}
