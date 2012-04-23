package net.bodz.lapiota.win32;

import java.net.URL;

import net.bodz.bas.loader.BundledLoader;
import net.bodz.bas.loader.LoadException;
import net.bodz.bas.loader.LoadUtil;
import net.bodz.bas.loader.UCL;
import net.bodz.bas.loader._LoadConfig;
import net.bodz.lapiota.nls.CLINLS;


public class JawinConfig
        extends _LoadConfig {

    @Override
    public ClassLoader getLoader(ClassLoader parent)
            throws LoadException {
        BundledLoader bl = BundledLoader.replace(parent);
        URL[] urls = LoadUtil.find("jawin"); //$NON-NLS-1$
        if (urls == null)
            throw new LoadException(CLINLS.getString("JawinConfig.cantFindJawin")); //$NON-NLS-1$
        UCL.addURL(bl, urls);
        return bl;
    }

}
