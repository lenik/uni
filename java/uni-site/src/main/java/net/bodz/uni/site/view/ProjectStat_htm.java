package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.util.IFontAwesomeCharAliases;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.ProjectStat;

public class ProjectStat_htm
        extends AbstractHtmlViewBuilder<ProjectStat>
        implements IFontAwesomeCharAliases {

    static final char ICO_FAVORITES = FA_HEART;
    static final char ICO_COMMENTS = FA_COMMENTS;
    static final char ICO_DOWNLOADS = FA_DOWNLOAD;
    static final String CLASS_FAVORITES = "icon-heart";
    static final String CLASS_COMMENTS = "icon-comments";
    static final String CLASS_DOWNLOADS = "icon-download-alt";

    public ProjectStat_htm() {
        super(ProjectStat.class);
    }

    @Override
    public IHtmlTag buildHtmlView(IHtmlViewContext ctx, IHtmlTag out, IUiRef<ProjectStat> ref, IOptions options)
            throws ViewBuilderException, IOException {
        ProjectStat stat = ref.get();

        out = out.div().class_("prj-stat fa");
        out.span().class_(CLASS_FAVORITES).text(stat.getFavorites().size());
        out.span().class_(CLASS_COMMENTS).text(stat.getComments().size());
        out.span().class_(CLASS_DOWNLOADS).text(stat.getDownloads().size());

        return out;
    }

}
