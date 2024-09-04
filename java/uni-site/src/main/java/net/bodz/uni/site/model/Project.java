package net.bodz.uni.site.model;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.textmap.I18nTextMapDocLoader;
import net.bodz.bas.io.res.IStreamInputSource;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.builtin.StringSource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.content.AbstractXjdocContent;
import net.bodz.bas.std.rfc.http.ICacheControl;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.VcsLogOptions;
import net.bodz.mda.xjdoc.model.IMutableElementDoc;

public class Project
        extends AbstractXjdocContent {

    static final Logger logger = LoggerFactory.getLogger(Project.class);

    Section section;
    String name;
    File directory;
    File docFile;
    String vcspath;

    ProjectStat projectStat;
    List<DownloadItem> downloadItems;

    Map<String, IVcsLogEntry> cachedLogs;
    Date logsExpires;

    public Project(Section section, String name, File directory) {
        this.section = section;
        this.name = name;
        this.directory = directory;

        docFile = new File(directory, name + ".itm");
        if (! docFile.exists()) {
            docFile = new File(directory, "." + name + ".itm");
            if (! docFile.exists())
                if (docFile == null)
                    throw new NullPointerException("docFile");
        }

        File rootDir = section.getSite().getBaseDir();
        vcspath = FilePath.getRelativePath(directory.getPath(), rootDir + "/");

        projectStat = new ProjectStat();

        downloadItems = new ArrayList<DownloadItem>();
        File debRepo = new File("/repo/deb");
        if (debRepo.isDirectory()) {
            // branch: stable, testing, unstable
            for (File branch : debRepo.listFiles())
                if (branch.isDirectory())
                    scanDebs(branch);
            Collections.reverse(downloadItems);
        }
    }

    void scanDebs(File branch) {
        for (String baseName : branch.list())
            if (baseName.startsWith(name + "_") && baseName.endsWith(".deb")) {
                File file = new File(branch, baseName);
                DownloadItem item = new DownloadItem();
                item.section = branch.getName();
                item.filename = baseName;
                item.href = "http://deb.bodz.net/" + branch.getName() + "/" + baseName;
                item.lastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()),
                        ZoneId.systemDefault());
                item.fileSize = file.length();
                downloadItems.add(item);
            }
    }

    @Override
    protected IMutableElementDoc loadXjdoc()
            throws ParseException, IOException {
        if (docFile == null)
            throw new NullPointerException("docFile");
        IStreamInputSource docRes;
        if (! docFile.exists()) {
            logger.warn("No doc file: " + docFile);
            docRes = new StringSource("");
        } else {
            docRes = new FileResource(docFile);
        }
        IMutableElementDoc doc = I18nTextMapDocLoader.load(docRes);
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

    public List<DownloadItem> getDownloadItems() {
        return downloadItems;
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

            // String trackFile = vcspath;
            // if (new File(directory, "VERSION.av").exists())
            // trackFile = vcspath + "/VERSION.av";

            for (IVcsLogEntry log : workingCopy.log(vcspath, logOptions))
                cachedLogs.put(log.getVersion(), log);
        }
        return cachedLogs;
    }

    /** ⇱ Implementation Of {@link ICacheControl}. */
    /* _____________________________ */static section.iface __CACHE__;

    @Override
    public int getMaxAge() {
        return 36 * 3600; // 1.5 days
    }

}
