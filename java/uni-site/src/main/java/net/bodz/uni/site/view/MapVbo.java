package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;

public class MapVbo
        extends AbstractHtmlViewBuilder<Map<?, ?>> {

    public MapVbo() {
        super(Map.class);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<Map<?, ?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();

        out.dd().start();

        for (Entry<?, ?> entry : ref.get().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            out.dt().start();
            out.text(key.toString());
            out.end(); // </dt>

            out.dd().start();
            embed(ctx, value);
            out.end(); // </dd>
        }

        out.end(); // </dd>
        return ctx;
    }

}
