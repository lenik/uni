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
    public void buildTitle(StringBuilder buffer, UniSite value) {
        ClassDoc classDoc = ClassDocLoader.load(value.getClass());
        String title = (String) classDoc.getTag("title");
        buffer.setLength(0);
        buffer.append(title);
    }

    @Override
    public IHtmlOutputContext buildHtmlView(IHtmlOutputContext ctx, IRefEntry<UniSite> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        UniSiteHrefs hrefs = UniSiteHrefs.getInstance();
        UniSite site = entry.get();

        boolean frameOnly = false;
        if (entry instanceof PathArrivalEntry) {
            IPathArrival arrival = ((PathArrivalEntry<UniSite>) entry).getArrival();
            if (arrival.getRemainingPath() != null)
                frameOnly = true;
        }

        out.head().start();
        {
            out.link().css("UniSite.css");
            out.link().css("UniSite-pink.css");
            out.script().javascriptSrc(hrefs.js + "jquery/jquery.js");
            out.endTag();
        }

        out.img().id("logo").src(hrefs.img + "button/text/uni-tools-100.png");

        out.img().src(hrefs.img + "hbar/angel-city.png").width("100%");
        out.hr().class_("line");

        out.div().id("main").start();

        // PropertyGUIRefMap refMap = explode(site);
        // refMap.importProperties();

        if (!frameOnly)
            indexBody(out, site);

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
        out.h1().text("List Of Projects");

        for (UniSection section : site.sectionMap.values()) {
            out.div().class_("uni-section").start();

            out.h2().start();
            out.a().href(section.getName() + "/").text(section.getName());
            out.text(" - " + section.getDescription());
            out.endTag(); // <h2>

            out.dl().class_("projects").start();
            for (UniProject project : section.getProjects()) {
                out.a().href(section.getName() + "/" + project.getName() + "/").start();
                out.dt().text(project.getName());
                out.endTag(); // <a>

                out.dd().text(project.getDescription());
            }
            out.endTag(); // <dl.projects>
            out.endTag(); // <div#uni-section>
            // out.hr();
        }
    }

}
