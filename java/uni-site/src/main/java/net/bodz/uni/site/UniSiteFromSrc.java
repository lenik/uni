package net.bodz.uni.site;

import java.io.File;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.err.IllegalConfigException;
import net.bodz.bas.t.ref.RuntimeValue;

public class UniSiteFromSrc
        extends RuntimeValue<UniSite> {

    private static final long serialVersionUID = 1L;

    private UniSite val = new UniSite(getUniDirFromSrc());

    @Override
    public Class<UniSite> getValueType() {
        return UniSite.class;
    }

    @Override
    public UniSite get() {
        return val;
    }

    public static File getUniDirFromSrc() {
        File pomDir = MavenPomDir.fromClass(UniSite.class).getBaseDir();
        File javaDir = pomDir.getParentFile();
        File uniDir = javaDir.getParentFile();
        if (uniDir == null || !uniDir.exists())
            throw new IllegalConfigException("Can't find base dir of the uni project.");
        return uniDir;
    }

}
