package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlTdTag;
import net.bodz.bas.html.dom.tag.HtmlTrTag;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.bas.ui.dom1.UiValue;

public class MapTableVbo<V>
        extends AbstractHtmlViewBuilder<Map<?, V>> {

    IHtmlViewBuilder<V> valueViewBuilder;

    public MapTableVbo(IHtmlViewBuilder<V> valueViewBuilder) {
        super(Map.class);
        this.valueViewBuilder = valueViewBuilder;
    }

    @Override
    public void preview(IHtmlViewContext ctx, IUiRef<Map<?, V>> ref, IOptions options) {
        for (Entry<?, V> entry : ref.get().entrySet()) {
            UiValue<V> valueRef = UiValue.wrap(entry.getValue());
            valueViewBuilder.preview(ctx, valueRef, options);
        }
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<Map<?, V>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlTag out = ctx.getOut();
        Map<?, V> map = ref.get();

        out = out.table();

        // this array list is used to keep the value order be same to the keys.
        List<V> values = new ArrayList<>();

        HtmlTrTag tr = out.thead().tr();
        for (Entry<?, V> entry : map.entrySet()) {
            values.add(entry.getValue());
            Object key = entry.getKey();
            tr.td().text(key.toString());
        }

        tr = out.tbody().tr();
        for (V value : values) {
            UiValue<V> valueRef = UiValue.wrap(value);
            HtmlTdTag td = tr.td();
            valueViewBuilder.buildHtmlView(ctx, valueRef, options);
        }
        return ctx;
    }

}
