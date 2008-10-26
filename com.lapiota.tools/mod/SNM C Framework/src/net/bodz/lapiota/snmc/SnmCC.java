package net.bodz.lapiota.snmc;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.lapiota.wrappers.BasicCLI;

/**
 * s.n.m. C-framework Console
 */
@Doc("Convert XML document to plain table")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
public class SnmCC extends BasicCLI {

    @Override
    protected void doMain(String[] args) throws Throwable {
    }

    public static void main(String[] args) throws Throwable {
        new SnmCC().run(args);
    }

}
