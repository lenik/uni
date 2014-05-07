package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.html.util.IFontAwesomeCharAliases;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.ProjectStat;

public class ProjectStatVbo
        extends AbstractHtmlViewBuilder<ProjectStat>
        implements IFontAwesomeCharAliases {

    static final char ICO_FAVORITES = FA_HEART;
    static final char ICO_COMMENTS = FA_COMMENTS;
    static final char ICO_DOWNLOADS = FA_DOWNLOAD;
    static final String CLASS_FAVORITES = "icon-heart";
    static final String CLASS_COMMENTS = "icon-comments";
    static final String CLASS_DOWNLOADS = "icon-download-alt";

    public ProjectStatVbo() {
        super(ProjectStat.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<ProjectStat> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        ProjectStat status = ref.get();

        out.div().class_("prj-status fa").start();
        out.span().class_(CLASS_FAVORITES).text(status.getFavorites().size());
        out.span().class_(CLASS_COMMENTS).text(status.getComments().size());
        out.span().class_(CLASS_DOWNLOADS).text(status.getDownloads().size());
        out.end(); // <div#prj-status>

        return ctx;
    }

}
