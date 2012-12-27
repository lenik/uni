package net.bodz.lapiota.batch.crypt;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.Certificate;

import net.bodz.bas.c.javax.security.auth.CertSelector;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.meta.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * KeyStore/Certificate Import
 */
@ProgramName("certimp")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class CertImport
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(CertImport.class);

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
    protected void mainImpl(String... args)
            throws Exception {
        if (args.length == 0)
            _help();

        KeyStore targetStore = target.getKeyStore(provider);

        for (String arg : args) {
            CertSelector srcCert = new CertSelector(arg);
            logger.mesg("import ", srcCert);
            String alias = srcCert.getCertAlias();
            Certificate cert = srcCert.getCertificate();
            targetStore.setCertificateEntry(alias, cert);
        }

        logger.mesg("save ", target);
        OutputStream out = new FileOutputStream(target.getStoreFile());
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
