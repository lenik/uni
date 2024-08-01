package net.bodz.lily.tool.daogen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.esm.util.NpmDir;
import net.bodz.bas.esm.util.NpmDirs;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.lily.tool.daogen.util.MavenDirs;

public class DirSearcher {

    static final Logger logger = LoggerFactory.getLogger(DirSearcher.class);

    public static final int MAVEN = 1;
    public static final int NPM = 2;

    Class<?> appClass;
    File startDir;
    int maxParents;

    public DirSearcher(Class<?> appClass, File startDir, int maxParents) {
        this.appClass = appClass;
        this.startDir = startDir;
        this.maxParents = maxParents;
    }

    public File findSiblingDir(String logTitle, int type, int index, String... search) {
        List<File> list = findSiblingDirs(logTitle, type, search);
        if (list == null || list.isEmpty())
            return null;

        if (index < 0)
            index = list.size() + index;
        if (index < 0 || index >= list.size())
            return null;
        File selection = list.get(index);
        return selection;
    }

    public List<File> findSiblingDirs(String logTitle, int type, String... search) {
        MavenPomDir startPomDir = MavenDirs.findPomDir(appClass, startDir);
        if (startPomDir == null)
            return null;

        String moduleName = startPomDir.getName();
        String prefix = "";
        int lastDash = moduleName.lastIndexOf('-');
        if (lastDash != -1)
            prefix = moduleName.substring(0, lastDash);

        String[] searchExpand = new String[search.length];
        for (int i = 0; i < search.length; i++) {
            String s = search[i];
            if (s.startsWith("-"))
                s = prefix + s;
            searchExpand[i] = s;
        }

        List<File> dirs = new ArrayList<>();

        if (type == MAVEN) {
            List<MavenPomDir> pomDirs = MavenDirs.findPomDirs(//
                    startPomDir.getBaseDir(), //
                    0 /* maxDepth */, maxParents, //
                    searchExpand);

            for (MavenPomDir pomDir : pomDirs)
                dirs.add(pomDir.getBaseDir());
        }

        else if (type == NPM) {
            List<NpmDir> npmDirs = NpmDirs.findPackageDirs(//
                    startPomDir.getBaseDir(), //
                    0 /* maxDepth */, maxParents, //
                    searchExpand);

            for (NpmDir npmDir : npmDirs)
                dirs.add(npmDir.getBaseDir());
        }

        for (File dir : dirs)
            logger.log(logTitle + ": " + dir);
        return dirs;
    }

}
