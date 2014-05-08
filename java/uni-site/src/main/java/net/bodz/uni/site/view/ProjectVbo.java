package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.List;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.view.util.RelativeTimeFormatter;

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

        out.h2().text("Download");
        out.div().class_("prj-panel").id("download").start();
        out.end(); // <div#download>

        out.h2().text("Comments");
        out.div().class_("prj-panel").id("comments").start();
        out.end(); // <div#comments>

        out.h2().text("Change Log");
        out.div().class_("prj-panel").id("changelog").start();

        try {
            out.ul().class_("changelog").start();
            List<IVcsLogEntry> logs = project.getLogs();
            for (IVcsLogEntry ent : logs) {
                String subject = ent.getSubject();
                boolean matching = subject.contains(project.getName());

                out.li().class_((matching ? "major" : "minor")).start();

                out.span().class_("author").text(ent.getAuthorName());
                out.span().text(": ");
                out.span().class_("subject").text(subject);

                long relativeTime = ent.getAuthorDate().getTimeInMillis() - System.currentTimeMillis();
                String relativeTimeStr = RelativeTimeFormatter.getInstance().format(relativeTime);
                out.span().class_("date").text("(" + relativeTimeStr + ")");

                out.end();
            }
            out.end(); // <ul.changelog>
        } catch (InterruptedException e) {
            throw new ViewBuilderException(e.getMessage(), e);
        }

        out.end(); // <div#comments>

        return ctx;
    }
}
