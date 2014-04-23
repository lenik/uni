package net.bodz.uni.site;

import java.io.IOException;

import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.html.AbstractHtmlViewBuilder;
import net.bodz.bas.repr.html.IHtmlOutputContext;
import net.bodz.bas.repr.html.IRequirements;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.path.PathArrivalEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.ClassDoc;

public class UniSiteVbo
        extends AbstractHtmlViewBuilder<UniSite> {

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
    public IHtmlOutputContext buildHtmlView(IHtmlOutputContext ctx, IRefEntry<UniSite> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();

        out.head().start();
        {
            out.link().type("text/css").rel("stylesheet").href("UniSite.css");
            out.link().type("text/css").rel("stylesheet").href("UniSite-blue.css");
            out.endTag();
        }

        UniSite site = entry.get();

        UniSiteHrefs hrefs = UniSiteHrefs.getInstance();
        out.img().id("logo").src(hrefs.img + "button/text/uni-tools-100.png");
        out.img().id("lead-bar").src(hrefs.img + "hbar/angel-city.png").width("100%");
        out.hr().class_("line");

        out.div().id("main").start();

        // out.h1().text(site.getLabel());
        ClassDoc classDoc = ClassDocLoader.load(site.getClass());
        out.p().text(classDoc.getTag("title"));

        // PropertyGUIRefMap refMap = explode(site);
        // refMap.importProperties();

        boolean body = true;
        if (entry instanceof PathArrivalEntry) {
            IPathArrival arrival = ((PathArrivalEntry<UniSite>) entry).getArrival();
            if (arrival.getRemainingPath() != null)
                body = false;
        }
        if (body)
            indexBody(out, site);

        out.endTag();

        return ctx;
    }

    @Override
    public void buildHtmlViewTail(IHtmlOutputContext ctx, IRefEntry<UniSite> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        UniSite site = entry.get();
        ClassDoc classDoc = ClassDocLoader.load(site.getClass());
        out.div().class_("copyright").text(classDoc.getTag("copyright"));
    }

    void indexBody(IHtmlOut out, UniSite site) {
        for (UniSection section : site.sectionMap.values()) {
            out.h2().start();
            out.a().href(section.getName() + "/").text(section.getName());
            out.text(" - " + section.getDescription());
            out.endTag();

            out.dl().start();
            for (UniProject project : section.getProjects()) {
                out.a().href(section.getName() + "/" + project.getName() + "/").start();
                out.dt().text(project.getName());
                out.endTag();

                out.dd().text(project.getDescription());
            }
            out.endTag();

            // out.hr();
        }
    }

}
