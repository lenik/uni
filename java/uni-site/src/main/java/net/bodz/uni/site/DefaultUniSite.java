package net.bodz.uni.site;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.INoPathRef;

public class DefaultUniSite
        implements INoPathRef {

    static final Logger logger = LoggerFactory.getLogger(DefaultUniSite.class);

    private UniSite uniSite;

    public DefaultUniSite() {
        Path dir = getUniDirFromSrc();
        if (dir == null) {
            logger.warn("Can't find base dir of the uni project.");
            dir = Paths.get("/mnt/istore/pro/uni");
        }
        uniSite = new UniSite(dir);
    }

    @Override
    public UniSite getTarget() {
        return uniSite;
    }

    public static Path getUniDirFromSrc() {
        MavenPomDir project = MavenPomDir.fromClass(UniSite.class);
        if (project == null) {
            logger.error("Can't find the maven project.");
            return null;
        }

        Path pomDir = project.getBaseDir();
        Path uniSiteProjDir;
        try {
            uniSiteProjDir = pomDir.toRealPath();
        } catch (IOException e) {
            logger.error("Bad base-dir: " + pomDir);
            return null;
        }
        Path javaDir = uniSiteProjDir.getParent();
        if (javaDir == null)
            return null;
        Path uniDir = javaDir.getParent();
        if (uniDir == null)
            return null;
        if (Files.notExists(uniDir))
            uniDir = null;
        return uniDir;
    }
}
