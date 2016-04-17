package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.io.IHtmlOut;
import net.bodz.bas.html.io.tag.HtmlDd;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.ui.dom1.IUiRef;

public class Map_htm
        extends AbstractHtmlViewBuilder<Map<?, ?>> {

    public Map_htm() {
        super(Map.class);
    }

    @Override
    public IHtmlOut buildHtmlViewStart(IHtmlViewContext ctx, IHtmlOut out, IUiRef<Map<?, ?>> ref)
            throws ViewBuilderException, IOException {
        out = out.dd();

        for (Entry<?, ?> entry : ref.get().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            out.dt().text(key.toString());

            HtmlDd dd = out.dd();
            embed(ctx, dd, value);
        }

        return out;
    }

}
