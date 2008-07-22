package net.bodz.lapiota.snmc;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.lapiota.wrappers.BasicCLI;

/**
 * s.n.m. C-framework Console
 */
@Doc("Convert XML document to plain table")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class SnmCC extends BasicCLI {

    @Override
    protected void _main(String[] args) throws Throwable {
    }

    public static void main(String[] args) throws Throwable {
        new SnmCC().climain(args);
    }

}
