package net.bodz.uni.site;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;

import net.bodz.bas.i18n.dom.StrFn;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.IPathDispatchable;
import net.bodz.bas.repr.path.ITokenQueue;
import net.bodz.bas.repr.path.PathArrival;
import net.bodz.bas.repr.path.PathDispatchException;
import net.bodz.bas.servlet.ctx.CurrentHttpService;
import net.bodz.bas.site.BasicSite;
import net.bodz.bas.site.org.ICrawlable;
import net.bodz.bas.site.org.ICrawler;
import net.bodz.bas.t.variant.IVariantMap;
import net.bodz.bas.vcs.IVcsWorkingCopy;
import net.bodz.bas.vcs.git.NativeGitVcsWorkingCopy;
import net.bodz.uni.site.model.Language;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Section;
import net.bodz.uni.site.model.ToolMenu;

import jakarta.servlet.http.HttpSession;

import section.iface;

/**
 * @label Uni - Development Tools
 * @label.zh.cn Uni 开发工具
 * @label.ja Uni 開発ツール
 * @copyright (ↄ) Copyleft 2004-2014 Lenik
 */
public class UniSite
        extends BasicSite {

    static final Logger logger = LoggerFactory.getLogger(UniSite.class);

    /** Root directory of the uni project. */
    private Path baseDir;
    private IVcsWorkingCopy workingCopy;

    private Map<String, Section> sectionMap = new TreeMap<>();
    private List<String> news;

    public String googleId;
    public String baiduId;

    public UniSite(Path baseDir) {
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

    public Path getBaseDir() {
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
        map.put(StrFn.wrap("Sites"), sites);
        map.put(StrFn.wrap("Help"), help);
        return map;
    }

    public synchronized void reload() {
        logger.info("Reload uni-site in the directory " + baseDir);
        sectionMap.clear();
        try (Stream<Path> stream = Files.list(baseDir)) {
            stream.forEach(sectionDir -> {

                if (Files.isDirectory(sectionDir))
                    return;

                String name = sectionDir.getFileName().toString();

                Section section = new Section(this, name, sectionDir);
                if (section.getDocFile() == null) {
                    logger.infof("Skipped non-section dir %s.", sectionDir);
                    return;
                }

                section.load();

                sectionMap.put(name, section);
            });
        } catch (IOException e) {
            logger.error("Error listing " + baseDir);
        }
    }

    /** ⇱ Implementation Of {@link IPathDispatchable}. */
    /* _____________________________ */static iface __PATH_DISP__;

    @Override
    public synchronized IPathArrival dispatch(IPathArrival previous, ITokenQueue tokens, IVariantMap<String> q)
            throws PathDispatchException {
        String token = tokens.peek();
        if (token == null)
            return null;

        Section section = sectionMap.get(token);
        if (section != null)
            return PathArrival.shift(previous, this, section, tokens);

        return super.dispatch(previous, tokens, q);
    }

    /** ⇱ Implementation Of {@link ICrawlable}. */
    /* _____________________________ */static iface __CRAWL__;

    @Override
    public void crawlableIntrospect(ICrawler crawler) {
        for (Entry<String, Section> entry : sectionMap.entrySet())
            crawler.follow(entry.getKey() + "/", entry.getValue());
    }

}
