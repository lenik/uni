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
    List<File> libDirs;

    MavenPomDir pomDir = MavenPomDir.fromClass(getClass());

    boolean addLibDirsFromLIB = false;
    boolean addLibDirsFromLANGLIB = true;
    boolean addLibDirsForExtension = true;
    Map<String, FileSearcher> fileSearcherByExt = new HashMap<>();

    public ResourceResolver() {
        String cwd = SystemProperties.getUserDir();
        String home = SystemProperties.getUserHome();
        if (home == null)
            home = cwd;

        this.configDir = new File(home, ".config" + fileSep + "catme");
    }

    public synchronized FileSearcher getFileSearcherForExtension(String extension)
            throws IOException {
        FileSearcher fileSearcher = fileSearcherByExt.get(extension);
        if (fileSearcher == null) {
            fileSearcher = buildFileSearcher(extension);
            fileSearcherByExt.put(extension, fileSearcher);
        }
        return fileSearcher;
    }

    FileSearcher buildFileSearcher(String extension)
            throws IOException {
        FileSearcher fileSearcher = new FileSearcher();
        if (libDirs != null) {
            for (File dir : libDirs)
                fileSearcher.addSearchDir(dir);
        }

        if (addLibDirsFromLIB) {
            String sysLibPath = System.getenv("LIB");
            if (sysLibPath != null)
                fileSearcher.addPathEnv(sysLibPath);
        }

        SrcLangType lang = SrcLangType.forExtension(extension);
        if (addLibDirsFromLANGLIB) {
            String langLibName = lang.name().toUpperCase() + "LIB";
            String langLibPath = System.getenv(langLibName.toUpperCase());
            if (langLibPath != null)
                fileSearcher.addPathEnv(langLibPath);
        }

        if (addLibDirsForExtension) {
            File sysPathDir = new File(pkgdatadir, "path" + fileSep + extension);
            fileSearcher.addPathDir(sysPathDir);
            File userPathDir = new File(configDir, "path" + fileSep + extension);
            fileSearcher.addPathDir(userPathDir);
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

        String extension = FilePath.getExtension(filename, false);

        if (pomDir != null) {
            File resdir = pomDir.getResourceDir(getClass());
            File resfile = new File(resdir, filename);
            if (resfile.exists())
                return new ResourceVariant(resfile);
        }

        URL url = getClass().getResource(filename);
        if (url != null)
            return new ResourceVariant(url);

        FileSearcher fileSearcher = getFileSearcherForExtension(extension);
        for (File file : fileSearcher.search(filename))
            return new ResourceVariant(file);

        return null;
    }

}
