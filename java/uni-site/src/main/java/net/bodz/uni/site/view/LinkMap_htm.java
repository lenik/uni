package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.io.IHtmlOut;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.ui.dom1.IUiRef;

public class LinkMap_htm
        extends AbstractHtmlViewBuilder<Map<?, ?>> {

    public LinkMap_htm() {
        super(Map.class);
    }

    @Override
    public IHtmlOut buildHtmlViewStart(IHtmlViewContext ctx, IHtmlOut out, IUiRef<Map<?, ?>> ref)
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
