package net.bodz.bas.vcs;

import java.io.IOException;

import net.bodz.bas.c.java.util.Dates;
import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
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
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<IVcsLogEntry> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        IVcsLogEntry ent = ref.get();

        IPathArrival __project_logs_entry = ctx.getPathArrival();
        Project project = (Project) __project_logs_entry.getPrevious(2).getTarget();

        IVcsWorkingCopy workingCopy = project.getSection().getSite().getWorkingCopy();

        out.div().text("Version: " + ent.getVersion());
        out.div().text("Author: " + ent.getAuthorName());
        out.div().text("Date: " + Dates.D10T8.format(ent.getAuthorDate().getTime()));

        out.ul().start();
        for (IFileChangement change : ent.getChanges()) {
            out.li().start();

            out.span().text(change.getStatus());
            out.span().text(change.getPath());

            switch (change.getStatus()) {
            case ADD:
            case MODIFY:
            case RENAME:
                out.pre().class_("prettyprint").start();
                try {
                    for (String line : workingCopy.getDiff(change.getPath(), ent.getVersion())) {
                        out.text(line);
                    }
                } catch (InterruptedException e) {
                    throw new ViewBuilderException("Error get diff: " + e.getMessage(), e);
                }
                out.end(); // <pre>

            default:
            }

            out.end(); // <li>
        }
        out.end(); // <ul>

        out.script().src(prettifySrc);
        return null;
    }

}
