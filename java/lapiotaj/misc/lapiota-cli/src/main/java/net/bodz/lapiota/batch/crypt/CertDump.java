package net.bodz.lapiota.batch.crypt;

import java.security.Provider;

import net.bodz.bas.c.javax.security.auth.CertSelector;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.sio.Stdio;

/**
 * KeyStore/Certificate Dump
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 1, 0 })
public class CertDump
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(CertDump.class);

    /**
     * Provider Class
     *
     * @option -p =PROV-CLASS
     */
    Provider provider = null;

    int detailLevel = 0;

    @Override
    protected void reconfigure()
            throws Exception {
        // INFO -> 0
        // DETAIL -> 1
        // DEBUG -> 2
        detailLevel = logger.getDelta();
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (args.length == 0)
            showHelpPage();
        for (String arg : args) {
            CertSelector cs = new CertSelector(arg, provider);
            cs.dump(Stdio.cout, detailLevel);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new CertDump().execute(args);
    }

}
