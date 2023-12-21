package net.bodz.lily.tool.daogen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class MavenDirs {

    static final Logger logger = LoggerFactory.getLogger(MavenDirs.class);

//    logger.debugf("find pomdirs in %s [depth=%d, parents=%d]", startDir, maxDepth, maxParents);

    public static List<MavenPomDir> findPomDirs(File startDir, int maxDepth, int maxParents, String... moduleNames) {
        List<MavenPomDir> foundPomDirs = new ArrayList<>(moduleNames.length);
        for (String name : moduleNames) {
            if (name == null)
                continue;
            MavenPomDir pomDir = findPomDir(startDir, maxDepth, maxParents, name);
            if (pomDir != null)
                foundPomDirs.add(pomDir);
        }
        return foundPomDirs;
    }

    public static MavenPomDir findPomDir(File startDir, int maxDepth, int maxParents, String moduleName) {
        if (moduleName == null)
            throw new NullPointerException("moduleName");

        // System.out.printf("dir %s, maxd=%d, maxp=%d, mod %s\n", startDir, maxDepth, maxParents, moduleName);
        File moduleDir = new File(startDir, moduleName);
        if (MavenPomDir.isPomDir(moduleDir)) {
            logger.debug("matched pom dir: " + moduleDir);
            return new MavenPomDir(moduleDir);
        }

        if (maxDepth > 0) {
            for (File subDir : startDir.listFiles((File f) -> f.isDirectory())) {
                MavenPomDir child = findPomDir(subDir, maxDepth - 1, 0, moduleName);
                if (child != null)
                    return child;
            }
        }

        if (maxParents <= 0)
            return null;
        File parent = startDir.getParentFile();
        if (parent == null)
            return null;

        return findPomDir(parent, maxDepth + 1, maxParents - 1, moduleName);
    }

    public static MavenPomDir findPomDir(Class<?> clazz, File startDir) {
        MavenPomDir pomDir = MavenPomDir.closest(startDir);
        if (pomDir == null) {
            // if (clazz == DaoCodeGenerator.class)
            // throw new RuntimeException("Can't locate the maven project from " + startDir);

            pomDir = MavenPomDir.fromClass(clazz);
            if (pomDir == null)
                throw new RuntimeException("Can't locate the maven project from " + clazz);
        }
        return pomDir;
    }

}
