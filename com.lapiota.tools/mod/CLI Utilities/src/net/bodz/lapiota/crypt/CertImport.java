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
import net.bodz.bas.sec.pki.util.CertSelector;
import net.bodz.bas.sec.pki.util.Providers;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("KeyStore/Certificate Import")
@ProgramName("certimp")
@RcsKeywords(id = "$Id: PKCS12Dump.java 39 2008-11-15 01:48:44Z lenik $")
@Version( { 0, 1 })
public class CertImport extends BasicCLI {

    @Option(alias = "p", vnam = "PROV-CLASS", doc = "Provider Class")
    @ParseBy(Providers.Parser.class)
    Provider     provider = null;

    @Option(alias = "t", vnam = "TARGET-CURL", required = true, doc = "target keystore where cert imports into")
    CertSelector target;

    @Override
    protected void _boot() throws Throwable {
    }

    @Override
    protected void doMain(String[] args) throws Throwable {
        if (args.length == 0)
            _help();

        KeyStore targetStore = target.getKeyStore(provider);

        for (String arg : args) {
            CertSelector srcCert = new CertSelector(arg);
            L.mesg("import ", srcCert);
            String alias = srcCert.getCertAlias();
            Certificate cert = srcCert.getCertificate();
            targetStore.setCertificateEntry(alias, cert);
        }

        L.mesg("save ", target);
        OutputStream out = Files.getOutputStream(target.getStoreFile());
        try {
            targetStore.store(out, target.getStorePassword().toCharArray());
        } finally {
            out.close();
        }
    }

    public static void main(String[] args) throws Throwable {
        new CertImport().run(args);
    }

}
