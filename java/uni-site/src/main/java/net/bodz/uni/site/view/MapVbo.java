package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlDdTag;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;

public class MapVbo
        extends AbstractHtmlViewBuilder<Map<?, ?>> {

    public MapVbo() {
        super(Map.class);
    }

    @Override
    public IHtmlTag buildHtmlView(IHtmlViewContext ctx, IHtmlTag out, IUiRef<Map<?, ?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        out = out.dd();

        for (Entry<?, ?> entry : ref.get().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            out.dt().text(key.toString());

            HtmlDdTag dd = out.dd();
            embed(ctx, dd, value);
        }

        return out;
    }

}
