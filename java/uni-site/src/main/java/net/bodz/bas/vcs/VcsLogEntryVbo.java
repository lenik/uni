package net.bodz.bas.vcs;

import java.io.IOException;

import net.bodz.bas.c.java.util.Dates;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlLiTag;
import net.bodz.bas.html.dom.tag.HtmlPreTag;
import net.bodz.bas.html.dom.tag.HtmlUlTag;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.path.IPathArrival;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.Project;

public class VcsLogEntryVbo
        extends AbstractHtmlViewBuilder<IVcsLogEntry> {

    static final String prettifySrc = "https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js";

    public VcsLogEntryVbo() {
        super(IVcsLogEntry.class);
    }

    @Override
    public IHtmlTag buildHtmlView(IHtmlViewContext ctx, IHtmlTag out, IUiRef<IVcsLogEntry> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IVcsLogEntry ent = ref.get();

        IPathArrival __project_logs_entry = ctx.query(IPathArrival.class);
        Project project = (Project) __project_logs_entry.getPrevious(2).getTarget();

        IVcsWorkingCopy workingCopy = project.getSection().getSite().getWorkingCopy();

        out.div().text("Version: " + ent.getVersion());
        out.div().text("Author: " + ent.getAuthorName());
        out.div().text("Date: " + Dates.D10T8.format(ent.getAuthorDate().getTime()));

        HtmlUlTag ul = out.ul();
        for (IFileChangement change : ent.getChanges()) {
            HtmlLiTag li = ul.li();

            li.span().text(change.getStatus());
            li.span().text(change.getPath());

            switch (change.getStatus()) {
            case ADD:
            case MODIFY:
            case RENAME:
                HtmlPreTag pre = li.pre().class_("prettyprint");
                try {
                    for (String line : workingCopy.getDiff(change.getPath(), ent.getVersion())) {
                        pre.text(line);
                    }
                } catch (InterruptedException e) {
                    throw new ViewBuilderException("Error get diff: " + e.getMessage(), e);
                }

            default:
            }

        }

        out.script().src(prettifySrc);
        return null;
    }

}
