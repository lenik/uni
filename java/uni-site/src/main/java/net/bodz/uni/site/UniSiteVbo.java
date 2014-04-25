package net.bodz.uni.site;

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
import net.bodz.uni.site.preference.Language;
import net.bodz.uni.site.preference.Theme;
import net.bodz.uni.site.preference.UserPreferences;

public class UniSiteVbo
        extends AbstractHtmlViewBuilder<UniSite>
        implements IUniSiteBasePaths {

    public UniSiteVbo() {
        super(UniSite.class);
    }

    @Override
    public void getRequirements(IRequirements requires) {
        // requires.add(CSS, "site", "1.0");
    }

    @Override
    public boolean isFrame() {
        return true;
    }

    @Override
    public void buildTitle(StringBuilder buffer, UniSite value) {
        ClassDoc classDoc = ClassDocLoader.load(value.getClass());
        String title = (String) classDoc.getTag("title");
        buffer.setLength(0);
        buffer.append(title);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<UniSite> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        UniSite site = entry.get();

        HttpSession session = ctx.getSession();
        UserPreferences pref = UserPreferences.fromSession(session);

        boolean frameOnly = false;
        if (entry instanceof PathArrivalEntry) {
            IPathArrival arrival = ((PathArrivalEntry<UniSite>) entry).getArrival();
            if (arrival.getRemainingPath() != null)
                frameOnly = true;
        }

        out.head().start();
        {
            out.link().css(_webApp_ + "UniSite.css");
            out.link().id("themeLink").css(_webApp_ + "UniSite-" + pref.getTheme().getSuffix() + ".css");
            out.script().javascript("var _webApp_ = " + StringQuote.qq(_webApp_));
            out.script().javascriptSrc(_js_ + "jquery/jquery.min.js");
            out.script().javascriptSrc(_webApp_ + "UniSite.js");
            out.end(); // <head>
        }

        out.div().class_("ui-menubar").start();
        {
            out.span().class_("ui-menu").id("m-tools").start();
            out.img().src(_img_ + "button/text/uni-tools-100.png"); // theme...?

            out.div().class_("ui-menubox").start();
            out.div().class_("ui-caption").text("Theme");

            out.div().class_("ui-menuitem").start();
            out.ul().class_("ui-enums").start();
            for (Theme theme : Theme.values()) {
                String themeLabel = theme.getXjdoc().getText().toString();
                boolean active = theme == pref.getTheme();
                out.li().class_(active ? "ui-active" : "").start();

                String script = String.format("setTheme(\"%s\", this)", theme.getSuffix());
                out.a().onclick(script).text(themeLabel);

                out.end(); // <li.ui-enumitem>
            }
            out.end(); // <ul.enums>
            out.end(); // <div.ui-menuitem>

            out.div().class_("ui-caption").text("Language");

            out.div().class_("ui-menuitem").start();
            out.ul().class_("ui-enums").start();
            for (Language lang : Language.values()) {
                String langLabel = lang.getXjdoc().getLabel().toString();
                boolean active = lang == pref.getLanguage();
                out.li().class_(active ? "ui-active" : "ui-inactive").start();
                if (active)
                    out.text(langLabel);
                else
                    out.a().href("javascript: setLanguage(this)").text(langLabel);
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
    public void buildHtmlViewTail(IHttpReprContext ctx, IRefEntry<UniSite> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        UniSite site = entry.get();
        ClassDoc classDoc = ClassDocLoader.load(site.getClass());

        out.end(); // <div#main>

        out.div().class_("copyright").text(classDoc.getTag("copyright"));
    }

    void indexBody(IHtmlOut out, UniSite site) {
        out.h1().text("List Of Projects");

        for (UniSection section : site.sectionMap.values()) {
            out.div().class_("uni-section").start();

            out.h2().start();
            out.a().href(section.getName() + "/").text(section.getName());
            out.text(" - " + section.getDescription());
            out.end(); // <h2>

            out.dl().class_("projects").start();
            for (UniProject project : section.getProjects()) {
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
