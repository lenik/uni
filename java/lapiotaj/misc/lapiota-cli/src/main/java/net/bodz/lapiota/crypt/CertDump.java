package net.bodz.lapiota.crypt;

import java.security.Provider;
import java.security.cert.CertSelector;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;

/**
 * KeyStore/Certificate Dump
 */
@RcsKeywords(id = "$Id$")
@Version({ 1, 0 })
public class CertDump
        extends BasicCLI {

    /**
     * Provider Class
     *
     * @option -p =PROV-CLASS
     */
    @ParseBy(Providers.Parser.class)
    Provider provider = null;

    int detailLevel = 0;

    @Override
    protected void _boot()
            throws Exception {
        // INFO -> 0
        // DETAIL -> 1
        // DEBUG -> 2
        detailLevel = L.getLevel() - LogTerm.INFO;
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        if (args.length == 0)
            _help();
        for (String arg : args) {
            CertSelector cs = new CertSelector(arg, provider);
            cs.dump(CharOuts.stdout, detailLevel);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new CertDump().run(args);
    }

}
