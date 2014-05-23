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

public class LinkMapVbo
        extends AbstractHtmlViewBuilder<Map<?, ?>> {

    public LinkMapVbo() {
        super(Map.class);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<Map<?, ?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Map<?, ?> map = ref.get();
        out.ul().start();

        for (Entry<?, ?> entry : map.entrySet()) {
            Object label = entry.getKey();
            Object href = entry.getValue();
            out.li().start();
            out.a().href(href.toString()).text(label);
            out.end(); // </li>
        }

        out.end(); // </ul>
        return ctx;
    }

}
