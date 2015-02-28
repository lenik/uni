package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.c.java.util.Dates;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlLiTag;
import net.bodz.bas.html.dom.tag.HtmlUlTag;
import net.bodz.bas.html.viz.AbstractHttpViewBuilder;
import net.bodz.bas.html.viz.IHtmlHeadData;
import net.bodz.bas.html.viz.IHttpViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.bas.vcs.IVcsLogEntry;
import net.bodz.uni.site.IUniSiteAnchors;
import net.bodz.uni.site.model.DebProject;
import net.bodz.uni.site.model.DownloadItem;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.util.RelativeTimeFormatter;

public class Project_htm
        extends AbstractHttpViewBuilder<Project>
        implements IUniSiteAnchors {

    public Project_htm() {
        super(Project.class);
    }

    @Override
    public void preview(IHttpViewContext ctx, IUiRef<Project> ref, IOptions options) {
        super.preview(ctx, ref, options);

        IHtmlHeadData metaData = ctx.getHeadData();
        Project project = ref.get();

        if (project instanceof DebProject) {
            DebProject deb = (DebProject) project;
            String debDescription = deb.getInfo().get("Description");
            metaData.setMeta(IHtmlHeadData.META_DESCRIPTION, debDescription);
        }
    }

    @Override
    public IHtmlTag buildHtmlView(IHttpViewContext ctx, IHtmlTag out, IUiRef<Project> ref, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        Project project = ref.get();
        String name = project.getName();

        String letter = name.substring(0, 1).toUpperCase();
        out.div().class_("prj-icon-large").text(letter);

        out = out.div().class_("project");

        out.h1().textf("Project %s", name);
        // embed(ctx, project.getStat());
        out.p().class_("description").text(project.getLabel());

        if (project instanceof DebProject) {
            DebProject deb = (DebProject) project;
            out.pre().text(deb.getInfo().get("Description"));
        }

        out.h2().text("Download");
        IHtmlTag panel = out.div().class_("panel").id("download");
        panel = panel.ul();
        for (DownloadItem item : project.getDownloadItems()) {
            HtmlLiTag li = panel.li();
            li.a().href(item.href).text(item.filename);
            li.text(" (" + item.fileSize + " bytes, " + Dates.YYYY_MM_DD.format(item.lastModified) + ")");
        }

        out.h2().text("Comments");
        out.div().class_("panel").id("comments");

        out.h2().text("Change Log");
        try {
            HtmlUlTag ul = out.ul().class_("panel").id("changelog");
            for (IVcsLogEntry ent : project.getLogs().values()) {
                String subject = ent.getSubject();
                boolean matching = subject.contains(project.getName());

                HtmlLiTag li = ul.li().class_((matching ? "major" : "minor"));

                li.span().class_("author").text(ent.getAuthorName());
                li.span().text(": ");
                /** @see VcsLogEntryVbo */
                li.a().class_("subject").href("logs/" + ent.getVersion()).text(subject);

                long relativeTime = ent.getAuthorDate().getTimeInMillis() - System.currentTimeMillis();
                String relativeTimeStr = RelativeTimeFormatter.getInstance().format(relativeTime);
                li.span().class_("date").text("(" + relativeTimeStr + ")");
            }
        } catch (InterruptedException e) {
            throw new ViewBuilderException(e.getMessage(), e);
        }

        return out;
    }

}
