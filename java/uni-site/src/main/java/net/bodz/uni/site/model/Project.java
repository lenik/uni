package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.i18n.dom1.MutableElement;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.VcsLogOptions;

public class Project
        extends MutableElement {

    Section section;
    File directory;
    String vcspath;

    ProjectStat projectStat;

    Map<String, IVcsLogEntry> cachedLogs;
    Date logsExpires;

    public Project(Section section, String name, File directory) {
        if (directory == null)
            throw new NullPointerException("directory");

        this.section = section;
        setName(name);
        this.directory = directory;

        File rootDir = section.getSite().getRootDir();
        vcspath = FilePath.getRelativePath(directory.getPath(), rootDir + "/");

        projectStat = new ProjectStat();
    }

    public Section getSection() {
        return section;
    }

    public File getDirectory() {
        return directory;
    }

    public String getVcsPath() {
        return vcspath;
    }

    public ProjectStat getStat() {
        return projectStat;
    }

    public synchronized Map<String, IVcsLogEntry> getLogs()
            throws IOException, InterruptedException {
        if (cachedLogs == null) {
            cachedLogs = new LinkedHashMap<>();
            IVcsWorkingCopy workingCopy = section.getSite().getWorkingCopy();
            VcsLogOptions logOptions = new VcsLogOptions();
            // logOptions.maxEntries = 100;
            logOptions.abbrVersionLength = 10;
            logOptions.includeChanges = true;
            for (IVcsLogEntry log : workingCopy.log(vcspath, logOptions))
                cachedLogs.put(log.getVersion(), log);
        }
        return cachedLogs;
    }

}
