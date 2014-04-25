package net.bodz.uni.site;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;

public class UniProjectVbo
        extends AbstractHtmlViewBuilder<UniProject> {

    public UniProjectVbo() {
        super(UniProject.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<UniProject> entry, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        UniProject project = entry.get();

        out.div().class_("prj-icon-large").text("IMAGE");

        out.h1().textf("Project %s", project.getName());

        out.div().class_("prj-status").start();
        out.text("H hh M mm D dd");
        out.end(); // <div#prj-status>

        String description = project.getDescription().toString();
        if (description != null)
            out.p().text(description);

        out.div().class_("prj-panel").id("download").start();
        out.text("Download");
        out.end(); // <div#download>

        out.div().class_("prj-panel").id("comments").start();
        out.text("Comments");
        out.end(); // <div#comments>

        return ctx;
    }

}
