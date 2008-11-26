package net.bodz.lapiota.crypt;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.lang.err.NotImplementedException;

import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.DeploySigningCertStore;

@BootInfo(syslibs = "deploy.jar")
@Doc("Java Deploy Certificates Manager")
@RcsKeywords(id = "$Id: Dcerts.java 0 2008-11-24 下午10:49:56 Shecti $")
@Version( { 0, 0 })
public class Dcerts extends BasicCLI {

    CertStore certStore;

    @Override
    protected void _boot() throws Throwable {
        if (certStore == null)
            certStore = DeploySigningCertStore.getUserCertStore();
        certStore.load();
    }

    @Override
    protected void doMain(String[] args) throws Throwable {
        for (Object cert : certStore.getCertificates()) {
            System.out.println(cert);
        }
        throw new NotImplementedException();
    }

    public static void main(String[] args) throws Throwable {
        new Dcerts().run(args);
    }

}
