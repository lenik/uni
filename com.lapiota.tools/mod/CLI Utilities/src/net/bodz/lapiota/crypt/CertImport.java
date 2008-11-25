package net.bodz.lapiota.crypt;

import java.io.OutputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.Certificate;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.bas.io.Files;
import net.bodz.bas.sec.pki.util.CertURL;
import net.bodz.bas.types.TypeParsers.GetInstanceParser;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("KeyStore/Certificate Import")
@ProgramName("certimp")
@RcsKeywords(id = "$Id: PKCS12Dump.java 39 2008-11-15 01:48:44Z lenik $")
@Version( { 0, 1 })
public class CertImport extends BasicCLI {

    @Option(alias = "p", vnam = "PROV-CLASS", doc = "Provider Class")
    @ParseBy(GetInstanceParser.class)
    Provider provider = null;

    @Option(alias = "t", vnam = "TARGET-CURL", required = true, doc = "target keystore where cert imports into")
    CertURL  target;

    @Override
    protected void _boot() throws Throwable {
    }

    @Override
    protected void doMain(String[] args) throws Throwable {
        if (args.length == 0)
            _help();

        KeyStore keyStore = target.getKeyStore();
        for (String arg : args) {
            CertURL curl = new CertURL(arg, provider);
            L.m.P("import ", curl);
            String alias = curl.getAlias();
            Certificate cert = curl.getCertificate();
            keyStore.setCertificateEntry(alias, cert);
        }

        L.m.P("save ", target);
        OutputStream out = Files.getOutputStream(target.getStoreURLFile());
        try {
            keyStore.store(out, target.getStorePassword().toCharArray());
        } finally {
            out.close();
        }
    }

    public static void main(String[] args) throws Throwable {
        new CertImport().run(args);
    }

}
