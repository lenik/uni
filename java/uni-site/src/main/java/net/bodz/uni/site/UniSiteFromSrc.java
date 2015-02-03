package net.bodz.uni.site;

import java.io.File;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.err.IllegalConfigException;
import net.bodz.bas.fn.IEvaluable;

public class UniSiteFromSrc
        implements IEvaluable<UniSite> {

    private UniSite val = new UniSite(getUniDirFromSrc());

    @Override
    public UniSite eval() {
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
