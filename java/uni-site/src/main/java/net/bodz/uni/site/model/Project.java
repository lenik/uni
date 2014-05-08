package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.i18n.dom1.MutableElement;
import net.bodz.bas.t.iterator.Iterables;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.VcsLogOptions;

public class Project
        extends MutableElement {

    Site site;
    File directory;
    ProjectStat projectStat;

    List<IVcsLogEntry> cachedLogs;
    Date logsExpires;

    public Project(Site site, String name, File directory) {
        if (site == null)
            throw new NullPointerException("site");
        if (name == null)
            throw new NullPointerException("name");
        if (directory == null)
            throw new NullPointerException("directory");

        this.site = site;
        setName(name);
        this.directory = directory;

        projectStat = new ProjectStat();
    }

    public File getDirectory() {
        return directory;
    }

    public ProjectStat getStat() {
        return projectStat;
    }

    public synchronized List<IVcsLogEntry> getLogs()
            throws IOException, InterruptedException {
        //if (cachedLogs == null) {
            File rootDir = site.getRootDir();
            String relativePath = FilePath.getRelativePath(directory.getPath(), rootDir + "/");
            IVcsWorkingCopy workingCopy = site.getWorkingCopy();
            VcsLogOptions logOptions = new VcsLogOptions();
            // logOptions.maxEntries = 100;
            Iterable<IVcsLogEntry> logs = workingCopy.log(relativePath, logOptions);
            cachedLogs = Iterables.toList(logs);
//        }
        return cachedLogs;
    }

}
