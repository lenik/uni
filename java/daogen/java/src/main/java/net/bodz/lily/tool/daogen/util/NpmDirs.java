package net.bodz.lily.tool.daogen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class NpmDirs {

    static final Logger logger = LoggerFactory.getLogger(NpmDirs.class);

    public static List<NpmDir> findPackageDirs(File startDir, int maxDepth, int maxParents, String... moduleNames) {
        List<NpmDir> foundNpmDirs = new ArrayList<>(moduleNames.length);
        for (String name : moduleNames) {
            if (name == null)
                continue;
            NpmDir npmDir = findNpmDir(startDir, maxDepth, maxParents, name);
            if (npmDir != null)
                foundNpmDirs.add(npmDir);
        }
        return foundNpmDirs;
    }

    public static NpmDir findNpmDir(File startDir, int maxDepth, int maxParents, String packageName) {
        if (packageName == null)
            throw new NullPointerException("packageName");

        // System.out.printf("dir %s, maxd=%d, maxp=%d, mod %s\n", startDir, maxDepth, maxParents, packageName);
        File packageDir = new File(startDir, packageName);
        if (NpmDir.isPackageDir(packageDir)) {
            logger.debug("matched package dir: " + packageDir);
            return new NpmDir(packageDir);
        }

        if (maxDepth > 0) {
            for (File subDir : startDir.listFiles((File f) -> f.isDirectory())) {
                NpmDir child = findNpmDir(subDir, maxDepth - 1, 0, packageName);
                if (child != null)
                    return child;
            }
        }

        if (maxParents <= 0)
            return null;
        File parent = startDir.getParentFile();
        if (parent == null)
            return null;

        return findNpmDir(parent, maxDepth + 1, maxParents - 1, packageName);
    }

}
