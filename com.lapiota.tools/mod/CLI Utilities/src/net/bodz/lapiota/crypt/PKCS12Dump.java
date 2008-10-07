package net.bodz.lapiota.crypt;

import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.Provider;
import java.security.KeyStore.Builder;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.sec.pki.util.ConsoleCallbackHandler;
import net.bodz.bas.types.TypeParsers;
import net.bodz.bas.types.util.Iterators;
import net.bodz.lapiota.a.ProgramName;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("PKCS#12 Certifaction Dump")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: ClassLauncher.java 21 2008-08-01 11:43:38Z lenik $")
@ProgramName("p12dump")
public class PKCS12Dump extends BasicCLI {

    @Option(alias = "p", doc = "Provider Class", parser = TypeParsers.GetInstanceParser.class)
    Provider               provider = null;

    @Option(alias = "P", doc = "PKCS#12 protection passphrase")
    String                 password;

    ConsoleCallbackHandler ch;

    @Override
    protected void _boot() throws Throwable {
        ch = new ConsoleCallbackHandler(L);
    }

    @Override
    protected void doFileArgument(File file, InputStream in) throws Throwable {
        KeyStore store;
        if (password != null) {
            if (provider == null)
                store = KeyStore.getInstance("PKCS12");
            else
                store = KeyStore.getInstance("PKCS12", provider);
            store.load(in, password.toCharArray());
        } else {
            CallbackHandlerProtection prot = new CallbackHandlerProtection(ch);
            Builder builder = Builder.newInstance("PKCS12", provider, file,
                    prot);
            // will ask password here.
            store = builder.getKeyStore();
        }
        dump(store);
    }

    void dump(KeyStore store) throws GeneralSecurityException {
        L.m.P(store.size(), " entries");
        for (String alias : Iterators.iterate(store.aliases())) {
            Certificate cert = store.getCertificate(alias);
            boolean keyEntry = store.isKeyEntry(alias);
            @SuppressWarnings("unused") Certificate[] certChain = store
                    .getCertificateChain(alias);
            Date creationDate = store.getCreationDate(alias);
            if (cert instanceof X509Certificate) {
                X509Certificate x509 = (X509Certificate) cert;
                Principal subjectDN = x509.getSubjectDN();
                L.i.P("    ", keyEntry ? '*' : ' ', //
                        " Cert ", subjectDN, //
                        " (", creationDate, ")");
            }
            L.d.P("        Data: ", cert);
        }
    }

    public static void main(String[] args) throws Throwable {
        new PKCS12Dump().run(args);
    }

}
