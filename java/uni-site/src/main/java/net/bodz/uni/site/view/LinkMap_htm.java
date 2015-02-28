package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.viz.AbstractHttpViewBuilder;
import net.bodz.bas.html.viz.IHttpViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;

public class LinkMap_htm
        extends AbstractHttpViewBuilder<Map<?, ?>> {

    public LinkMap_htm() {
        super(Map.class);
    }

    @Override
    public IHtmlTag buildHtmlView(IHttpViewContext ctx, IHtmlTag out, IUiRef<Map<?, ?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        Map<?, ?> map = ref.get();
        out = out.ul();

        for (Entry<?, ?> entry : map.entrySet()) {
            Object label = entry.getKey();
            Object href = entry.getValue();
            out.li().a().href(href.toString()).text(label);
        }

        return out;
    }

}
