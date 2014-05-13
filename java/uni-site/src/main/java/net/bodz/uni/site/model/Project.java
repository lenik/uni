package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.text.textmap.I18nTextMapDocLoader;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.VcsLogOptions;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.AbstractXjdocElement;

public class Project
        extends AbstractXjdocElement {

    Section section;
    String name;
    File directory;
    File docFile;
    String vcspath;

    ProjectStat projectStat;

    Map<String, IVcsLogEntry> cachedLogs;
    Date logsExpires;

    public Project(Section section, String name, File directory) {
        this.section = section;
        this.name = name;
        this.directory = directory;

        docFile = new File(directory, name + ".itm");
        if (!docFile.exists()) {
            docFile = new File(directory, "." + name + ".itm");
            if (!docFile.exists())
                docFile = null;
        }

        File rootDir = section.getSite().getRootDir();
        vcspath = FilePath.getRelativePath(directory.getPath(), rootDir + "/");

        projectStat = new ProjectStat();
    }

    @Override
    protected IElementDoc loadXjdoc()
            throws ParseException, IOException {
        if (!docFile.exists())
            throw new IOException("No doc file: " + docFile);
        IElementDoc doc = I18nTextMapDocLoader.load(new FileResource(docFile));
        return doc;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public String getName() {
        return name;
    }

    public File getDirectory() {
        return directory;
    }

    public String getVcsPath() {
        return vcspath;
    }

    public boolean isPrivate() {
        return false;
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
