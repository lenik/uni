package net.bodz.uni.site;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import net.bodz.bas.html.HtmlViewBuilder;
import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;
import net.bodz.bas.site.org.ICrawlable;
import net.bodz.bas.site.org.ICrawler;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.git.NativeGitVcsWorkingCopy;
import net.bodz.lily.site.LilyStartSite;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Section;
import net.bodz.uni.site.model.ToolMenu;
import net.bodz.uni.site.view.UniSiteVbo;

/**
 * @label Uni - Development Tools
 * @label.zh.cn Uni 开发工具
 * @label.ja Uni 開発ツール
 * @copyright (ↄ) Copyleft 2004-2014 Lenik
 */
@HtmlViewBuilder(UniSiteVbo.class)
public class UniSite
        extends LilyStartSite {

    static final Logger logger = LoggerFactory.getLogger(UniSite.class);

    /** Root directory of the uni project. */
    private File baseDir;
    private IVcsWorkingCopy workingCopy;

    private Map<String, Section> sectionMap = new TreeMap<>();
    private List<String> news;

    public String googleId;
    public String baiduId;

    public UniSite(File baseDir) {
        this.baseDir = baseDir;
        workingCopy = new NativeGitVcsWorkingCopy(baseDir);
        reload();
    }

    public synchronized void reload() {
        sectionMap.clear();
        for (File sectionDir : baseDir.listFiles()) {
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

    public File getBaseDir() {
        return baseDir;
    }

    public IVcsWorkingCopy getWorkingCopy() {
        return workingCopy;
    }

    public synchronized Map<String, Section> getSectionMap() {
        return sectionMap;
    }

    public Preferences getPreferences() {
        HttpSession session = CurrentHttpService.getSession();
        Preferences preferences = Preferences.fromSession(session);
        return preferences;
    }

    public ToolMenu getToolMenu() {
        return new ToolMenu(this);
    }

    @Override
    public String getHomeUrl() {
        return "http://uni.bodz.net";
    }

    /** ⇱ Implementation Of {@link IPathDispatchable}. */
    /* _____________________________ */static section.iface __PATH_DISP__;

    @Override
    public synchronized IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens)
            throws PathDispatchException {
        String token = tokens.peek();
        Section section = sectionMap.get(token);
        if (section != null)
            return PathArrival.shift(previous, section, tokens);

        return super.dispatch(previous, tokens);
    }

    /** ⇱ Implementation Of {@link ICrawlable}. */
    /* _____________________________ */static section.iface __CRAWL__;

    @Override
    public void crawlableIntrospect(ICrawler crawler) {
        for (Entry<String, Section> entry : sectionMap.entrySet())
            crawler.follow(entry.getKey() + "/", entry.getValue());
    }

}
