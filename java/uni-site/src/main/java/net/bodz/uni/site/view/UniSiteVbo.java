package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlMetaData;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.html.artifact.IArtifactDependency;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.*;
import net.bodz.bas.i18n.dom1.IElement;
import net.bodz.bas.potato.ref.UiPropertyRefMap;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.PathArrivalEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.t.pojo.Pair;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.ClassDoc;
import net.bodz.uni.site.IUniSiteAnchors;
import net.bodz.uni.site.UniSite;
import net.bodz.uni.site.model.Language;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.model.Section;

public class UniSiteVbo
        extends AbstractHtmlViewBuilder<UniSite>
        implements IUniSiteAnchors {

    public UniSiteVbo() {
        super(UniSite.class);
    }

    @Override
    public boolean isOrigin(UniSite value) {
        return true;
    }

    @Override
    public boolean isFrame() {
        return true;
    }

    @Override
    public void preview(IHtmlViewContext ctx, IUiRef<UniSite> ref, IOptions options) {
        super.preview(ctx, ref, options);

        IHtmlMetaData metaData = ctx.getMetaData();
        metaData.setMeta(IHtmlMetaData.META_AUTHOR, "谢继雷 (Xiè Jìléi)");
        metaData.setMeta(IHtmlMetaData.META_VIEWPORT, "width=device-width, initial-scale=1");

        metaData.addDependency("jquery-min", IArtifactDependency.SCRIPT);
        metaData.addDependency("font-awesome", IArtifactDependency.STYLESHEET).setPriority(IArtifactDependency.LOW);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<UniSite> ref, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlTag out = ctx.getOut();
        UniSite site = ref.get();

        HttpSession session = ctx.getSession();
        Preferences pref = Preferences.fromSession(session);

        UiPropertyRefMap propMap = explode(ref);

        IPathArrival arrival = (IPathArrival) ctx.getRequest().getAttribute(IPathArrival.class.getName());
        boolean frameOnly = arrival.getPrevious(site).getRemainingPath() != null;

        HtmlHeadTag head = out.head();
        {
            writeHeadMetas(ctx, head);
            writeHeadImports(ctx, head);

            // lang alternatives
            for (Entry<String, Pair<Language, String>> entry : getAltLangHrefs(ctx.getRequest()).entrySet()) {
                Pair<Language, String> langHref = entry.getValue();
                Language lang = langHref.getKey();
                if (lang != null && lang.isFullTranslated())
                    head.link().rel("alternate").hreflang(entry.getKey()).href(langHref.getValue());
            }

            // stylesheets
            head.link().css(_webApp_ + "site.css");
            head.link().css(_webApp_ + "theme-" + pref.getTheme() + ".css").id("themeLink");

            // scripts
            head.script().javascript("var _webApp_ = " + StringQuote.qq(_webApp_));
            head.script().javascriptSrc(_webApp_ + "site.js");
        }

        out = out.body();

        HtmlDivTag menubar = out.div().class_("ui-menubar");
        {
            HtmlSpanTag span = menubar.span().class_("ui-menu").id("m-tools");
            span.div().id("toolbox").text("Uni Tools");
            embed(ctx, span, propMap.get("toolMenu"));
        }

        out.img().src(_img_ + "hbar/angel-city.png").width("100%");
        out.hr().class_("line");

        if (ref instanceof PathArrivalEntry) {
            IHtmlTag nav = out.nav().ul();
            for (IPathArrival a : arrival.toList()) {
                Object target = a.getTarget();

                String label;
                if (target instanceof IElement)
                    label = ((IElement) target).getLabel().toString();
                else
                    label = target.toString();

                String href = _webApp_.join(a.getConsumedFullPath() + "/").toString();
                nav.li().a().href(href).text(label);
            }
        }

        HtmlDivTag mainDiv = out.div().id("main");
        if (!frameOnly)
            indexBody(mainDiv, site);

        ClassDoc classDoc = ClassDocLoader.load(site.getClass());

        HtmlDivTag foot = out.div().class_("foot");
        {
            HtmlDivTag sectDiv = foot.div().class_("list");
            sectDiv.span().text("Section: ");
            for (Section section : site.getSectionMap().values()) {
                String href = _webApp_.join(section.getName() + "/").toString();
                sectDiv.a().href(href).text(section.getName());
            }

            HtmlDivTag langDiv = foot.div();
            langDiv.span().text("Language: ");
            for (Pair<Language, String> langHref : getAltLangHrefs(ctx.getRequest()).values()) {
                Language lang = langHref.getKey();
                if (lang == null)
                    continue;
                langDiv.a().href(langHref.getValue()).text(lang.getXjdoc().getText());
            }

            HtmlDivTag powerDiv = foot.div();
            powerDiv.span().text("Powered by: ");
            powerDiv.a().href(_webApp_.join("modules/bas-site/").toString()).text("BAS Site Framework 2.0");

            foot.text(classDoc.getTag("copyright").toString());
        }

        ctx.setOut(mainDiv);
        return ctx;
    }

    void indexBody(IHtmlTag out, UniSite site) {
        HtmlH1Tag h1 = out.h1().text("List Of Projects");
        h1.a().style("cursor: pointer").onclick("reloadSite()").text("[Reload]");

        for (Section section : site.getSectionMap().values()) {
            HtmlDivTag div = out.div().class_("uni-section").id(section.getName());

            HtmlH2Tag h2 = div.h2();
            h2.a().href(section.getName() + "/").text(section.getName());
            h2.text(" - " + section.getLabel());

            HtmlDlTag dl = div.dl().class_("uni-projects");
            for (Project project : section.getProjects()) {
                if (project.isPrivate())
                    continue;
                HtmlATag a = dl.a().href(section.getName() + "/" + project.getName() + "/");
                a.dt().text(project.getName());

                dl.dd().text(project.getLabel());
            }
        }
    }

    Map<String, Pair<Language, String>> getAltLangHrefs(HttpServletRequest request) {
        Map<String, Pair<Language, String>> map = new LinkedHashMap<>();

        String path = request.getPathInfo();
        if (path.startsWith("/intl/")) {
            int slash2 = path.indexOf('/', 6);
            path = path.substring(slash2);

            String defaultHref = _webApp_ + path.substring(1);
            map.put("x-default", Pair.of((Language) null, defaultHref));
        }
        for (Language lang : Language.values()) {

            String href = _webApp_.join("intl/") + lang.getCode() + path;
            map.put(lang.getCode(), Pair.of(lang, href));
        }

        return map;
    }

}
