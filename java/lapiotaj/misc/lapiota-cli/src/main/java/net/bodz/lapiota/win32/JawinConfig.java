package net.bodz.lapiota.win32;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.net.URL;

import net.bodz.bas.c.java.net.URLClassLoaders;
import net.bodz.bas.loader.BundledLoader;
import net.bodz.bas.loader.LoadException;
import net.bodz.bas.loader.LoadUtil;
import net.bodz.bas.loader._LoadConfig;

public class JawinConfig
        extends _LoadConfig {

    @Override
    public ClassLoader getLoader(ClassLoader parent)
            throws LoadException {
        BundledLoader bl = BundledLoader.replace(parent);
        URL[] urls = LoadUtil.find("jawin");
        if (urls == null)
            throw new LoadException(CLINLS.getString("JawinConfig.cantFindJawin"));
        URLClassLoaders.addURL(bl, urls);
        return bl;
    }

}
