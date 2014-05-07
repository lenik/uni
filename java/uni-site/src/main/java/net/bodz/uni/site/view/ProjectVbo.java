package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.Project;

public class ProjectVbo
        extends AbstractHtmlViewBuilder<Project> {

    public ProjectVbo() {
        super(Project.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<Project> ref, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        Project project = ref.get();

        String name = project.getName();
        String description = project.getDescription().toString();

        String letter = name.substring(0, 1).toUpperCase();
        out.div().class_("prj-icon-large").text(letter);

        out.h1().textf("Project %s", name);

        embed(ctx, project.getStat());

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
