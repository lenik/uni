package net.bodz.lapiota.javatools;

import java.security.*;
import java.security.KeyStore.Builder;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.Provider.Service;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.LDAPCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.ExemptionMechanism;
import javax.security.auth.callback.CallbackHandler;

import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.java.util.TreeTextMap;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.text.codec.builtin.HexCodec;
import net.bodz.bas.util.iter.Iterables;
import net.bodz.bas.util.order.ComparableComparator;

import com.sun.security.auth.callback.TextCallbackHandler;

/**
 * Dump Java CLI Environment
 */
@SuppressWarnings("restriction")
@ProgramName("jenv")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class CLIEnviron
        extends BasicCLI {

    /**
     * Dump environ variables
     *
     * @option -e
     */
    void dumpEnv() {
        Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for (Object key : keys) {
            String value = env.get(key);
            L.mesg(key, " = ", value);
        }
        L.mesg();
    }

    /**
     * Dump system properties
     *
     * @option -p
     */
    void dumpProperties() {
        dump("System", System.getProperties(), 1);
        L.mesg();
    }

    /**
     * Dump security providers
     *
     * @option -s
     */
    boolean dumpProviders;

    /**
     * password for all keystores (default empty)
     *
     * @option -P
     */
    String password = "";

    CallbackHandler ch;

    @Override
    protected void _boot()
            throws Exception {
        ch = new TextCallbackHandler();
    }

    void dumpRest() {
        if (dumpProviders)
            dumpProviders();
    }

    void dumpProviders() {
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++) {
            Provider provider = providers[i];
            dumpProvider(provider);
        }
    }

    void dumpProvider(Provider provider) {
        L.mesgf("Provider %s (%.2f): %s\n", provider.getName(), provider.getVersion(), provider.getInfo());
        dump(provider.getName(), provider, 1);
        List<Service> services = new ArrayList<Service>(provider.getServices());
        Comparator<Service> cmp = new Comparator<Service>() {
            @Override
            public int compare(Service a, Service b) {
                int c = a.getType().compareTo(b.getType());
                if (c != 0)
                    return c;
                c = a.getAlgorithm().compareTo(b.getAlgorithm());
                if (c != 0)
                    return c;
                c = a.getClassName().compareTo(b.getClassName());
                return c;
            }
        };
        Collections.sort(services, cmp);
        TextMap<Set<Service>> types = new TreeTextMap<Set<Service>>();
        for (Service service : services) {
            String type = service.getType();
            String alg = service.getAlgorithm();
            L.mesgf("  Service %s(%s): %s\n", type, alg, service.getClassName());
            Set<Service> servsInType = types.get(type);
            if (servsInType == null)
                types.put(type, servsInType = new HashSet<Service>());
            servsInType.add(service);
        }

        Set<Service> certStores = types.get("CertStore");
        if (certStores != null)
            for (Service storeServ : certStores) {
                assert provider == storeServ.getProvider();
                String storeType = storeServ.getAlgorithm();
                L.mesg("  CertStore ", storeType, ": ");
                try {
                    CertStore store;
                    CertStoreParameters csparams = null;
                    // if (password != null)
                    if ("Collection".equals(storeType)) {
                        csparams = new CollectionCertStoreParameters();
                    } else if ("com.sun.security.IndexedCollection".equals(storeType)) {
                        csparams = new CollectionCertStoreParameters();
                    } else if ("LDAP".equals(storeType)) {
                        csparams = new LDAPCertStoreParameters();
                    }
                    store = CertStore.getInstance(storeType, csparams, provider);
                    Collection<? extends Certificate> certs = store.getCertificates(null);
                    L.mesg(certs.size(), " entries");
                    for (Certificate cert : certs) {
                        dumpCert("    ", cert, null);
                    }
                } catch (Exception e) {
                    L.error(e);
                }
            }
        Set<Service> keyStores = types.get("KeyStore");
        if (keyStores != null)
            for (Service storeServ : keyStores) {
                assert provider == storeServ.getProvider();
                String storeType = storeServ.getAlgorithm();
                // String storeClass = storeServ.getClassName();
                L.mesg("  KeyStore ", storeType, ": ");
                try {
                    KeyStore store;
                    if (password != null) {
                        store = KeyStore.getInstance(storeType, provider);
                        store.load(null, password.toCharArray());
                    } else {
                        Builder builder = Builder.newInstance(storeType, provider, new CallbackHandlerProtection(ch));
                        // will ask password here.
                        store = builder.getKeyStore();
                    }
                    L.info(store.size(), " entries");
                    for (String alias : Iterables.otp(store.aliases())) {
                        Certificate cert = store.getCertificate(alias);
                        // @SuppressWarnings("unused")
                        // Certificate[] certChain = store
                        // .getCertificateChain(alias);
                        Date creationDate = store.getCreationDate(alias);
                        dumpCert("    ", cert, creationDate);
                    }
                } catch (Exception e) {
                    L.error(e);
                }
            }

        Set<Service> ciphers = types.get("Cipher");
        if (ciphers != null)
            for (Service cipherServ : ciphers) {
                assert provider == cipherServ.getProvider();
                String cipherAlg = cipherServ.getAlgorithm();
                // String cipherClass = cipherServ.getClassName();
                L.info("  Cipher ", cipherAlg, ": ");
                Cipher cipher = null;
                try {
                    cipher = Cipher.getInstance(cipherAlg, provider);
                } catch (GeneralSecurityException e) {
                    L.error(e);
                }
                L.info(cipher.toString());
                int blockSize = cipher.getBlockSize();
                byte[] iv = cipher.getIV();
                AlgorithmParameters params = cipher.getParameters();
                ExemptionMechanism mech = cipher.getExemptionMechanism();
                L.info("    block-size = ", blockSize);
                if (iv != null)
                    L._info(1, "    IV = ", HexCodec.getInstance().encode(iv));
                if (params != null)
                    L.info("    parameters = ", params);
                if (mech != null)
                    L.info("    exemption = ", mech.getName());
            }

        L.mesg();
    }

    void dumpCert(String prefix, Certificate cert, Date creationDate) {
        L.info(prefix, "Cert ", cert.getType(), ": ");
        X509Certificate x509 = null;
        if (cert instanceof X509Certificate)
            x509 = (X509Certificate) cert;
        if (x509 != null)
            L.info(x509.getSubjectDN());
        if (creationDate != null)
            L.info(" <", creationDate, ">");
        L.info();
        L.info(prefix, "    Data: ", cert);
    }

    public void dump(String title, Properties properties, int indent) {
        String prefix = Strings.repeat(4 * indent, ' ');
        L.mesg(prefix, title, " properties");
        List<Object> keys = new ArrayList<Object>(properties.keySet());
        Collections.sort(keys, ComparableComparator.getRawInstance());
        for (Object key : keys) {
            Object value = properties.get(key);
            L.mesg(prefix, "    ", key, " = ", value);
        }
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        dumpRest();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            L.mesgf("%4d. %s;\n", i, arg);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new CLIEnviron().execute(args);
    }

}
