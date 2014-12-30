package net.bodz.uni.site;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import net.bodz.bas.html.meta.HtmlViewBuilder;
import net.bodz.bas.http.ctx.CurrentHttpService;
import net.bodz.bas.i18n.dom.iString;
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
import net.bodz.uni.site.model.Language;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Section;
import net.bodz.uni.site.model.ToolMenu;
import net.bodz.uni.site.view.UniSiteVbo;

import com.tinylily.site.LilyStartSite;

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

    @Override
    public String getSiteUrl() {
        return "http://uni.bodz.net";
    }

    @Override
    protected Map<String, String> getAlternateUrls() {
        Map<String, String> alternateUrls = new LinkedHashMap<String, String>();
        for (Language lang : Language.values())
            if (lang.isFullTranslated())
                alternateUrls.put(lang.getCode(), getSiteUrl() + "/intl/" + lang.getCode());
        return alternateUrls;
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

    public Map<iString, Map<String, String>> getFootLinkMap() {
        Map<iString, Map<String, String>> map = new LinkedHashMap<>();
        Map<String, String> sites = new LinkedHashMap<String, String>();
        sites.put("Jazz Framework", "http://jazz.bodz.net");
        sites.put("Uni Tools", "http://uni.bodz.net");
        Map<String, String> help = new LinkedHashMap<>();
        help.put("About", "about/");
        help.put("Contacts", "contacts/");
        map.put(iString.fn.val("Sites"), sites);
        map.put(iString.fn.val("Help"), help);
        return map;
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
