package net.bodz.lapiota.javatools;

import static net.bodz.bas.text.encodings.Encodings.HEX;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.Provider;
import java.security.Security;
import java.security.KeyStore.Builder;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.Provider.Service;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.ExemptionMechanism;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.sec.pki.util.ConsoleCallbackHandler;
import net.bodz.bas.types.TextMap;
import net.bodz.bas.types.TextMap.TreeTextMap;
import net.bodz.bas.types.util.Comparators;
import net.bodz.bas.types.util.Iterators;
import net.bodz.bas.types.util.Strings;
import net.bodz.lapiota.a.ProgramName;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("Dump Java CLI Environment")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@ProgramName("jenv")
public class CLIEnviron extends BasicCLI {

    @Option(alias = "e", doc = "Dump environ variables")
    void dumpEnv() {
        Map<String, String> env = System.getenv();
        List<String> keys = new ArrayList<String>(env.keySet());
        Collections.sort(keys);
        for (Object key : keys) {
            String value = env.get(key);
            L.m.P(key, " = ", value);
        }
        L.m.P();
    }

    @Option(alias = "p", doc = "Dump system properties")
    void dumpProperties() {
        dump("System", System.getProperties(), 1);
        L.m.P();
    }

    @Option(alias = "s", doc = "Dump security providers")
    boolean                dumpProviders;

    @Option(alias = "P", doc = "password for all keystores (default empty)")
    String                 password = "";

    ConsoleCallbackHandler ch;

    @Override
    protected void _boot() throws Throwable {
        ch = new ConsoleCallbackHandler(L);
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
        L.m.PF("Provider %s (%.2f): %s", provider.getName(), provider
                .getVersion(), provider.getInfo());
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
            L.m.PF("  Service %s(%s): %s", type, alg, service.getClassName());
            Set<Service> servsInType = types.get(type);
            if (servsInType == null)
                types.put(type, servsInType = new HashSet<Service>());
            servsInType.add(service);
        }

        Set<Service> stores = types.get("KeyStore");
        if (stores != null)
            for (Service storeServ : stores) {
                assert provider == storeServ.getProvider();
                String storeType = storeServ.getAlgorithm();
                // String storeClass = storeServ.getClassName();
                L.m.p("  KeyStore ", storeType, ": ");
                try {
                    KeyStore store;
                    if (password != null) {
                        store = KeyStore.getInstance(storeType, provider);
                        store.load(null, password.toCharArray());
                    } else {
                        Builder builder = Builder.newInstance(storeType,
                                provider, new CallbackHandlerProtection(ch));
                        // will ask password here.
                        store = builder.getKeyStore();
                    }
                    L.m.P(store.size(), " entries");
                    for (String alias : Iterators.iterate(store.aliases())) {
                        Certificate cert = store.getCertificate(alias);
                        @SuppressWarnings("unused") Certificate[] certChain = store
                                .getCertificateChain(alias);
                        Date creationDate = store.getCreationDate(alias);
                        if (cert instanceof X509Certificate) {
                            X509Certificate x509 = (X509Certificate) cert;
                            Principal subjectDN = x509.getSubjectDN();
                            L.i.P("    Cert ", subjectDN, //
                                    " (", creationDate, ")");
                        }
                        L.d.P("        Data: ", cert);
                    }
                } catch (Exception e) {
                    L.e.P(e);
                }
            }

        Set<Service> ciphers = types.get("Cipher");
        if (ciphers != null)
            for (Service cipherServ : ciphers) {
                assert provider == cipherServ.getProvider();
                String cipherAlg = cipherServ.getAlgorithm();
                // String cipherClass = cipherServ.getClassName();
                L.i.p("  Cipher ", cipherAlg, ": ");
                Cipher cipher = null;
                try {
                    cipher = Cipher.getInstance(cipherAlg, provider);
                } catch (GeneralSecurityException e) {
                    L.e.P(e);
                }
                L.d.P(cipher.toString());
                int blockSize = cipher.getBlockSize();
                byte[] iv = cipher.getIV();
                AlgorithmParameters params = cipher.getParameters();
                ExemptionMechanism mech = cipher.getExemptionMechanism();
                L.d.P("    block-size = ", blockSize);
                if (iv != null)
                    L.d.P("    IV = ", HEX.encode(iv));
                if (params != null)
                    L.d.P("    parameters = ", params);
                if (mech != null)
                    L.d.P("    exemption = ", mech.getName());
            }

        L.m.P();
    }

    public void dump(String title, Properties properties, int indent) {
        String prefix = Strings.repeat(4 * indent, ' ');
        L.m.P(prefix, title, " properties");
        List<Object> keys = new ArrayList<Object>(properties.keySet());
        Collections.sort(keys, Comparators.STD);
        for (Object key : keys) {
            Object value = properties.get(key);
            L.m.P(prefix, "    ", key, " = ", value);
        }
    }

    @Override
    protected void _main(String[] args) throws Throwable {
        dumpRest();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            L.m.PF("%4d. %s;", i, arg);
        }
    }

    public static void main(String[] args) throws Throwable {
        new CLIEnviron().run(args);
    }

}
