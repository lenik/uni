package net.bodz.uni.site.view;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlMetaData;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.html.artifact.IArtifactDependency;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.UiPropertyRefMap;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.PathArrivalEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.ClassDoc;
import net.bodz.uni.site.IUniSiteAnchors;
import net.bodz.uni.site.UniSite;
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
        IHtmlOut out = ctx.getOut();
        UniSite site = ref.get();

        HttpSession session = ctx.getSession();
        Preferences pref = Preferences.fromSession(session);

        UiPropertyRefMap propMap = explode(ref);

        boolean frameOnly = false;
        if (ref instanceof PathArrivalEntry) {
            IPathArrival arrival = ((PathArrivalEntry<UniSite>) ref).getArrival();
            if (arrival.getRemainingPath() != null)
                frameOnly = true;
        }

        out.html().start();
        out.head().start();
        {
            writeHeadMetas(ctx);
            writeHeadImports(ctx);

            // stylesheets
            out.link().css(_webApp_ + "site.css");
            out.link().id("themeLink").css(_webApp_ + "theme-" + pref.getTheme() + ".css");

            // scripts
            out.script().javascript("var _webApp_ = " + StringQuote.qq(_webApp_));
            out.script().javascriptSrc(_webApp_ + "site.js");

            out.end(); // <head>
        }

        out.div().class_("ui-menubar").start();
        {
            out.span().class_("ui-menu").id("m-tools").start();
            out.div().id("toolbox").text("Uni Tools");
            embed(ctx, propMap.get("toolMenu"));
            out.end(); // <span.ui-menu#m-tools>
            out.end(); // <divl#menubar>
        }

        out.img().src(_img_ + "hbar/angel-city.png").width("100%");
        out.hr().class_("line");

        out.div().id("main").start();

        if (!frameOnly)
            indexBody(out, site);

        return ctx;
    }

    @Override
    public void buildHtmlViewTail(IHtmlViewContext ctx, IUiRef<UniSite> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        UniSite site = entry.get();
        ClassDoc classDoc = ClassDocLoader.load(site.getClass());

        out.end(); // <div#main>

        out.div().class_("copyright").text(classDoc.getTag("copyright"));

        out.end(); // <html>
    }

    void indexBody(IHtmlOut out, UniSite site) {
        out.h1().text("List Of Projects").start();
        out.a().style("cursor: pointer").onclick("reloadSite()").text("[Reload]");
        out.end();

        for (Section section : site.getSectionMap().values()) {
            out.div().class_("uni-section").id(section.getName()).start();

            out.h2().start();
            out.a().href(section.getName() + "/").text(section.getName());
            out.text(" - " + section.getLabel());
            out.end(); // <h2>

            out.dl().class_("uni-projects").start();
            for (Project project : section.getProjects()) {
                if (project.isPrivate())
                    continue;
                out.a().href(section.getName() + "/" + project.getName() + "/").start();
                out.dt().text(project.getName());
                out.end(); // <a>

                out.dd().text(project.getLabel());
            }
            out.end(); // <dl.projects>
            out.end(); // <div#uni-section>
            // out.hr();
        }
    }

}
