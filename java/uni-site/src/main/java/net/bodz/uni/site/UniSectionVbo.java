package net.bodz.uni.site;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;

public class UniSectionVbo
        extends AbstractHtmlViewBuilder<UniSection> {

    public UniSectionVbo() {
        super(UniSection.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<UniSection> entry, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        UniSection section = entry.get();

        out.h1().textf("Section %s", section.getName());

        String description = section.getDescription().toString();
        if (description != null)
            out.p().text(description);

        for (UniProject project : section.getProjects()) {
            out.div().class_("uni-project").start();
            out.div().class_("prj-icon").text("IMAGE");

            out.div().class_("prj-description").start();

            out.a().href(project.getName() + "/").start();
            out.text(project.getName());
            out.endTag(); // <a>

            out.div().class_("prj-status").start();
            out.text("H hh M mm D dd");
            out.endTag(); // <div#prj-status>

            out.text(project.getDescription().toString());
            out.endTag();

            out.endTag(); // <div#uni-project>
        }

        return ctx;
    }

}
