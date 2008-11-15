package net.bodz.lapiota.eclipse.jdt;

import java.net.URL;

import net.bodz.bas.loader.BundledLoader;
import net.bodz.bas.loader.UCL;
import net.bodz.bas.loader._LoadConfig;
import net.bodz.bas.snm.SJEclipse;

public class JdtConfig extends _LoadConfig {

    static String[] libs = {
                         //
            "org.eclipse.jdt.core", //
            "org.eclipse.text", //
            "org.eclipse.equinox.common", //
            "org.eclipse.core.resources", //
            "org.eclipse.core.jobs", //
            "org.eclipse.core.runtime", //
            "org.eclipse.osgi", //
            "org.eclipse.core.contenttype", //
            "org.eclipse.equinox.preferences", //
                         };

    @Override
    public ClassLoader getLoader(ClassLoader parent) {
        BundledLoader bl = BundledLoader.replace(parent);
        URL[] urls = new URL[libs.length];
        for (int i = 0; i < libs.length; i++) {
            String lib = libs[i] + "_";
            URL url = SJEclipse.findlib(lib);
            if (url == null)
                throw new Error("Can't find eclipse lib: " + lib);
            urls[i] = url;
        }
        return UCL.addOrCreate(bl, urls);
    }
}
