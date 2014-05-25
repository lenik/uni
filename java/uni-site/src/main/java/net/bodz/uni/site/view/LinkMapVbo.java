package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;

public class LinkMapVbo
        extends AbstractHtmlViewBuilder<Map<?, ?>> {

    public LinkMapVbo() {
        super(Map.class);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<Map<?, ?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlTag out = ctx.getOut();
        Map<?, ?> map = ref.get();
        out = out.ul();

        for (Entry<?, ?> entry : map.entrySet()) {
            Object label = entry.getKey();
            Object href = entry.getValue();
            out.li().a().href(href.toString()).text(label);
        }

        return ctx;
    }

}
