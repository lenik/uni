package net.bodz.uni.site.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.git.NativeGitVcsWorkingCopy;
import net.bodz.lily.site.LilyStartSite;

/**
 * @label The Uni Site
 * @title Uni - Deveoper's Tools
 * @copyright (ↄ) Copyleft 2004-2014 Lenik
 */
public class Site
        extends LilyStartSite
        implements IPathDispatchable {

    static final Logger logger = LoggerFactory.getLogger(Site.class);

    /** Root directory of the uni project. */
    private File rootDir;
    private IVcsWorkingCopy workingCopy;

    private Map<String, Section> sectionMap = new TreeMap<>();
    private List<String> news;

    public String googleId;
    public String baiduId;

    public Site(File rootDir) {
        this.rootDir = rootDir;
        workingCopy = new NativeGitVcsWorkingCopy(rootDir);
        reload();
    }

    public synchronized void reload() {
        sectionMap.clear();
        for (File sectionDir : rootDir.listFiles()) {
            if (!sectionDir.isDirectory())
                continue;

            String name = sectionDir.getName();

            Section section = new Section(this, name, sectionDir);
            if (section.getDocFile() == null)
                continue;

            section.load();

            sectionMap.put(name, section);
        }
    }

    public File getRootDir() {
        return rootDir;
    }

    public IVcsWorkingCopy getWorkingCopy() {
        return workingCopy;
    }

    public synchronized Map<String, Section> getSectionMap() {
        return sectionMap;
    }

    /** ⇱ Implementation Of {@link IPathDispatchable}. */
    /* _____________________________ */static section.iface __PATH_DISP__;

    @Override
    public synchronized IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens)
            throws PathDispatchException {
        String token = tokens.peek();
        Section section = sectionMap.get(token);
        if (section == null)
            return null;
        else
            return PathArrival.shift(previous, section, tokens);
    }

}
