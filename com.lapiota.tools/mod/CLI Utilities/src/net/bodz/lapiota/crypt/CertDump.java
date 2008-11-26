package net.bodz.lapiota.crypt;

import java.security.Provider;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.log.ALog;
import net.bodz.bas.sec.pki.util.CertDumper;
import net.bodz.bas.sec.pki.util.CertSelector;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("KeyStore/Certificate Dump")
@RcsKeywords(id = "$Id$")
@Version( { 1, 0 })
public class CertDump extends BasicCLI {

    @Option(alias = "p", vnam = "PROV-CLASS", doc = "Provider Class")
    @ParseBy(Providers.Parser.class)
    Provider provider = null;

    int      detail   = CertDumper.SIMPLE;

    @Override
    protected void _boot() throws Throwable {
        if (L.getLevel() >= ALog.DEBUG)
            detail = CertDumper.FULL;
        else if (L.getLevel() >= ALog.DETAIL)
            detail = CertDumper.DETAIL;
    }

    @Override
    protected void doMain(String[] args) throws Throwable {
        if (args.length == 0)
            _help();
        for (String arg : args) {
            CertSelector curl = new CertSelector(arg, provider);
            curl.dump(CharOuts.stdout, detail);
        }
    }

    public static void main(String[] args) throws Throwable {
        new CertDump().run(args);
    }

}
