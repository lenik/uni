package net.bodz.uni.site.view;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.html.IRequirements;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.PathArrivalEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.ClassDoc;
import net.bodz.uni.site.IBasePaths;
import net.bodz.uni.site.model.Language;
import net.bodz.uni.site.model.Preferences;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.model.Section;
import net.bodz.uni.site.model.Site;
import net.bodz.uni.site.model.Theme;

public class SiteVbo
        extends AbstractHtmlViewBuilder<Site>
        implements IBasePaths {

    public SiteVbo() {
        super(Site.class);
    }

    @Override
    public void getRequirements(IRequirements requires) {
        // requires.add(CSS, "site", "1.0");
    }

    @Override
    public boolean isOrigin(Site value) {
        return true;
    }

    @Override
    public boolean isFrame() {
        return true;
    }

    @Override
    public void buildTitle(StringBuilder buffer, Site value) {
        ClassDoc classDoc = ClassDocLoader.load(value.getClass());
        String title = (String) classDoc.getTag("title");
        buffer.setLength(0);
        buffer.append(title);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<Site> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Site site = entry.get();

        HttpSession session = ctx.getSession();
        Preferences pref = Preferences.fromSession(session);

        boolean frameOnly = false;
        if (entry instanceof PathArrivalEntry) {
            IPathArrival arrival = ((PathArrivalEntry<Site>) entry).getArrival();
            if (arrival.getRemainingPath() != null)
                frameOnly = true;
        }

        out.head().start();
        {
            out.link().css(_webApp_ + "site.css");
            out.link().id("themeLink").css(_webApp_ + "theme-" + pref.getTheme().getSuffix() + ".css");
            out.script().javascript("var _webApp_ = " + StringQuote.qq(_webApp_));
            out.script().javascriptSrc(_js_ + "jquery/jquery.min.js");
            out.script().javascriptSrc(_webApp_ + "site.js");
            out.end(); // <head>
        }

        out.div().class_("ui-menubar").start();
        {
            out.span().class_("ui-menu").id("m-tools").start();

            out.a().style("text-decoration: none").href(_webApp_.toString()).start();
            out.div().id("toolbox").text("Uni Tools");
            out.end();

            out.div().class_("ui-menubox").start();
            out.div().class_("ui-caption").text("Theme");

            out.div().class_("ui-menuitem").start();
            out.ul().class_("ui-enums").start();
            for (Theme theme : Theme.values()) {
                String themeLabel = theme.getXjdoc().getText().toString();
                boolean active = theme == pref.getTheme();
                out.li().class_(active ? "ui-active" : "").start();

                String script = String.format("setTheme(\"%s\", \"%s\")", theme.name(), theme.getSuffix());
                out.a().attr("value", theme.name()).onclick(script).text(themeLabel);

                out.end(); // <li>
            }
            out.end(); // <ul.enums>
            out.end(); // <div.ui-menuitem>

            out.div().class_("ui-caption").text("Language");

            out.div().class_("ui-menuitem").start();
            out.ul().class_("ui-enums").start();
            for (Language lang : Language.values()) {
                String langLabel = lang.getXjdoc().getLabel().toString();
                boolean active = lang == pref.getLanguage();
                out.li().class_(active ? "ui-active" : "").start();

                String script = String.format("setLanguage(\"%s\")", lang.name());
                out.a().attr("value", lang.name()).onclick(script).text(langLabel);

                out.end(); // <li>
            }
            out.end(); // <ul.enums>
            out.end(); // <div.ui-menuitem>

            out.div().class_("ui-caption").text("Caching");
            out.div().class_("ui-menuitem").text("ON OFF");

            out.end(); // <span#m-tools>
            out.end(); // <span.ui-menu>
            out.end(); // <divl#menubar>
        }

        out.img().src(_img_ + "hbar/angel-city.png").width("100%");
        out.hr().class_("line");

        out.div().id("main").start();

        // PropertyGUIRefMap refMap = explode(site);
        // refMap.importProperties();

        if (!frameOnly)
            indexBody(out, site);

        return ctx;
    }

    @Override
    public void buildHtmlViewTail(IHttpReprContext ctx, IRefEntry<Site> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Site site = entry.get();
        ClassDoc classDoc = ClassDocLoader.load(site.getClass());

        out.end(); // <div#main>

        out.div().class_("copyright").text(classDoc.getTag("copyright"));
    }

    void indexBody(IHtmlOut out, Site site) {
        out.h1().text("List Of Projects").start();
        out.a().style("cursor: pointer").onclick("reloadSite()").text("[Reload]");
        out.end();

        for (Section section : site.getSectionMap().values()) {
            out.div().class_("uni-section").start();

            out.h2().start();
            out.a().href(section.getName() + "/").text(section.getName());
            out.text(" - " + section.getLabel());
            out.end(); // <h2>

            out.dl().class_("uni-projects").start();
            for (Project project : section.getProjects()) {
                out.a().href(section.getName() + "/" + project.getName() + "/").start();
                out.dt().text(project.getName());
                out.end(); // <a>

                out.dd().text(project.getDescription());
            }
            out.end(); // <dl.projects>
            out.end(); // <div#uni-section>
            // out.hr();
        }
    }

}
