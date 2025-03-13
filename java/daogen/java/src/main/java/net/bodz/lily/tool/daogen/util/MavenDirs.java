package net.bodz.lily.tool.daogen.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.java.nio.file.PathFn;
import net.bodz.bas.c.java.util.stream.iterable.Stream;
import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class MavenDirs {

    static final Logger logger = LoggerFactory.getLogger(MavenDirs.class);

//    logger.debugf("find pomdirs in %s [depth=%d, parents=%d]", startDir, maxDepth, maxParents);

    public static List<MavenPomDir> findPomDirs(Path startDir, int maxDepth, int maxParents, String... moduleNames) {
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

    public static MavenPomDir findPomDir(Path startDir, int maxDepth, int maxParents, String moduleName) {
        if (moduleName == null)
            throw new NullPointerException("moduleName");

        // System.out.printf("dir %s, maxd=%d, maxp=%d, mod %s\n", startDir, maxDepth, maxParents,
        // moduleName);
        Path moduleDir = startDir.resolve(moduleName);
        if (MavenPomDir.isPomDir(moduleDir)) {
            logger.debug("matched pom dir: " + moduleDir);
            return new MavenPomDir(moduleDir);
        }

        if (maxDepth > 0) {
            try (Stream<Path> stream = PathFn.list(startDir)) {
                for (Path subDir : stream)
                    if (Files.isDirectory(subDir)) {
                        MavenPomDir child = findPomDir(subDir, maxDepth - 1, 0, moduleName);
                        if (child != null)
                            return child;
                    }
            } catch (IOException e) {
                // ignore. stop checking children.
            }
        }

        if (maxParents <= 0)
            return null;
        Path parent = startDir.getParent();
        if (parent == null)
            return null;

        return findPomDir(parent, maxDepth + 1, maxParents - 1, moduleName);
    }

    public static MavenPomDir findPomDir(Class<?> clazz, Path startDir) {
        MavenPomDir pomDir = MavenPomDir.closest(startDir);
        if (pomDir == null) {
            pomDir = MavenPomDir.fromClass(clazz);
            if (pomDir == null)
                throw new RuntimeException("Can't locate the maven project from " + clazz);
        }
        return pomDir;
    }

}
