package net.bodz.uni.catme.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.m2.MavenPomDir;
import net.bodz.bas.c.system.SystemProperties;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.catme.SrcLangType;

public class ResourceResolver {

    static final Logger logger = LoggerFactory.getLogger(ResourceResolver.class);

    static final String fileSep = SystemProperties.getFileSeparator();

    File pkgdatadir = new File("/usr/share/catme");
    File configDir;

    public boolean searchWorkDir = false;
    File workDir;
    public boolean searchHomeDir = false;
    File homeDir;

    public boolean searchClassResources;

    public boolean searchPomDir = true;
    MavenPomDir pomDir = MavenPomDir.fromClass(getClass());

    List<File> libDirs;
    public boolean searchLibDirs = true;
    public boolean searchLibDirsForExtension;

    public boolean searchEnvLangLIBs;
    public boolean searchEnvLIB;

    Map<String, FileSearcher> byExtension = new HashMap<>();

    public ResourceResolver() {
        String cwd = SystemProperties.getUserDir();
        if (cwd != null)
            workDir = new File(cwd);

        String home = SystemProperties.getUserHome();
        if (home != null)
            homeDir = new File(home);

        if (home == null)
            home = cwd;

        this.configDir = new File(home, ".config" + fileSep + "catme");
    }

    public synchronized FileSearcher getFileSearcherForExtension(String extension)
            throws IOException {
        FileSearcher fileSearcher = byExtension.get(extension);
        if (fileSearcher == null) {
            fileSearcher = buildFileSearcher(extension);
            byExtension.put(extension, fileSearcher);
        }
        return fileSearcher;
    }

    FileSearcher buildFileSearcher(String extension)
            throws IOException {
        FileSearcher fileSearcher = new FileSearcher();

        if (searchWorkDir && workDir != null)
            fileSearcher.addSearchDir(workDir);

        if (searchHomeDir && homeDir != null)
            fileSearcher.addSearchDir(homeDir);

        if (searchPomDir)
            if (pomDir != null) {
                File mainResourceDir = pomDir.getResourceDir(getClass());
                fileSearcher.addSearchDir(mainResourceDir);
            }

        if (searchLibDirs)
            if (libDirs != null) {
                for (File dir : libDirs)
                    fileSearcher.addSearchDir(dir);
            }

        if (searchLibDirsForExtension) {
            File sysPathDir = new File(pkgdatadir, "path" + fileSep + extension);
            fileSearcher.addPathDir(sysPathDir);
            File userPathDir = new File(configDir, "path" + fileSep + extension);
            fileSearcher.addPathDir(userPathDir);
        }

        if (searchEnvLangLIBs) {
            SrcLangType lang = SrcLangType.forExtension(extension);
            String langLibName = lang.name().toUpperCase() + "LIB";
            String langLibPath = System.getenv(langLibName.toUpperCase());
            if (langLibPath != null)
                fileSearcher.addPathEnv(langLibPath);
        }

        if (searchEnvLIB) {
            String sysLibPath = System.getenv("LIB");
            if (sysLibPath != null)
                fileSearcher.addPathEnv(sysLibPath);
        }

        return fileSearcher;
    }

    public String loadTextResource(String filename)
            throws IOException {
        ResourceVariant res = findResource(filename);
        if (res == null) {
            logger.error("Can't find resource " + filename);
            return null;
        }
        String text = res.toResource().read().readString();
        return text;
    }

    public ResourceVariant findResource(String filename)
            throws IOException {
        if (filename.startsWith("/")) {
            File file = new File(filename);
            if (!file.exists())
                return null;
            return new ResourceVariant(file);
        }

        if (searchClassResources) {
            URL url = getClass().getResource(filename);
            if (url != null)
                return new ResourceVariant(url);
        }

        String extension = FilePath.getExtension(filename, false);
        FileSearcher fileSearcher = getFileSearcherForExtension(extension);
        for (File file : fileSearcher.search(filename))
            return new ResourceVariant(file);

        return null;
    }

}
