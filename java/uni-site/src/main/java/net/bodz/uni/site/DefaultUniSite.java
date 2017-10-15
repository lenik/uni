package net.bodz.uni.site;

import java.io.File;
import java.io.IOException;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.INoPathRef;

public class DefaultUniSite
        implements INoPathRef {

    static final Logger logger = LoggerFactory.getLogger(DefaultUniSite.class);

    private UniSite uniSite;

    public DefaultUniSite() {
        File dir = getUniDirFromSrc();
        if (dir == null) {
            logger.warn("Can't find base dir of the uni project.");
            dir = new File("/mnt/istore/pro/uni");
        }
        uniSite = new UniSite(dir);
    }

    @Override
    public UniSite getTarget() {
        return uniSite;
    }

    public static File getUniDirFromSrc() {
        File pomDir = MavenPomDir.fromClass(UniSite.class).getBaseDir();
        File uniSiteProjDir;
        try {
            uniSiteProjDir = pomDir.getCanonicalFile();
        } catch (IOException e) {
            logger.error("Bad base-dir: " + pomDir);
            return null;
        }
        File javaDir = uniSiteProjDir.getParentFile();
        if (javaDir == null)
            return null;
        File uniDir = javaDir.getParentFile();
        if (uniDir == null)
            return null;
        if (!uniDir.exists())
            uniDir = null;
        return uniDir;
    }
}
