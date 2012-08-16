package net.bodz.lapiota.crypt;

import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;

/**
 * KeyStore/Certificate Import
 */
@ProgramName("certimp")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class CertImport
        extends BasicCLI {

    /**
     * Provider Class
     *
     * @option -p =PROV-CLASS
     */
    Provider provider = null;

    /**
     * Target keystore where cert imports into
     *
     * @option -t =TARGET-CURL required
     */
    CertSelector target;

    @Override
    protected void _boot()
            throws Exception {
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
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

    public static void main(String[] args)
            throws Exception {
        new CertImport().execute(args);
    }

}
