package net.bodz.lapiota.crypt;

import java.security.cert.CertStore;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;

/**
 * "Java Deploy Certificates Manager"
 */
@BootInfo(syslibs = "deploy.jar")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class Dcerts
        extends BasicCLI {

    CertStore certStore;

    @Override
    protected void _boot()
            throws Exception {
        if (certStore == null)
            certStore = DeploySigningCertStore.getUserCertStore();
        certStore.load();
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        for (Object cert : certStore.getCertificates()) {
            System.out.println(cert);
        }
        throw new NotImplementedException();
    }

    public static void main(String[] args)
            throws Exception {
        new Dcerts().execute(args);
    }

}
