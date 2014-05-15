package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlMetaData;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.uni.site.IUniSiteAnchors;
import net.bodz.uni.site.model.DebProject;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.util.RelativeTimeFormatter;

public class ProjectVbo
        extends AbstractHtmlViewBuilder<Project>
        implements IUniSiteAnchors {

    public ProjectVbo() {
        super(Project.class);
    }

    @Override
    public void preview(IHtmlViewContext ctx, IUiRef<Project> ref, IOptions options) {
        super.preview(ctx, ref, options);

        IHtmlMetaData metaData = ctx.getMetaData();
        Project project = ref.get();

        if (project instanceof DebProject) {
            DebProject deb = (DebProject) project;
            String debDescription = deb.getInfo().get("Description");
            metaData.setMeta(IHtmlMetaData.META_DESCRIPTION, debDescription);
        }
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<Project> ref, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        Project project = ref.get();

        String name = project.getName();

        String letter = name.substring(0, 1).toUpperCase();
        out.div().class_("prj-icon-large").start();
        {
            String sectionName = project.getSection().getName();
            out.a().href(_webApp_ + sectionName + "#" + letter).start();
            out.text(letter);
            out.end(); // <a>
            out.end(); // <div.prj-icon-large>
        }

        out.div().class_("project").start();

        out.h1().textf("Project %s", name);
        // embed(ctx, project.getStat());
        out.p().class_("description").text(project.getLabel());

        if (project instanceof DebProject) {
            DebProject deb = (DebProject) project;
            out.pre().text(deb.getInfo().get("Description"));
        }

        out.h2().text("Download");
        out.div().class_("panel").id("download").start();
        out.end(); // <div#download>

        out.h2().text("Comments");
        out.div().class_("panel").id("comments").start();
        out.end(); // <div#comments>

        out.h2().text("Change Log");
        try {
            out.ul().class_("panel").id("changelog").start();
            for (IVcsLogEntry ent : project.getLogs().values()) {
                String subject = ent.getSubject();
                boolean matching = subject.contains(project.getName());

                out.li().class_((matching ? "major" : "minor")).start();

                out.span().class_("author").text(ent.getAuthorName());
                out.span().text(": ");
                /** @see VcsLogEntryVbo */
                out.a().class_("subject").href("logs/" + ent.getVersion()).text(subject);

                long relativeTime = ent.getAuthorDate().getTimeInMillis() - System.currentTimeMillis();
                String relativeTimeStr = RelativeTimeFormatter.getInstance().format(relativeTime);
                out.span().class_("date").text("(" + relativeTimeStr + ")");

                out.end();
            }
            out.end(); // <ul.changelog>
        } catch (InterruptedException e) {
            throw new ViewBuilderException(e.getMessage(), e);
        }

        out.end(); // div.project
        return ctx;
    }
}
