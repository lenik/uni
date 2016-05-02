package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.io.IHtmlOut;
import net.bodz.bas.html.io.tag.HtmlTd;
import net.bodz.bas.html.io.tag.HtmlTr;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.bas.ui.dom1.UiValue;

public class MapTable_htm<V>
        extends AbstractHtmlViewBuilder<Map<?, V>> {

    IHtmlViewBuilder<V> valueViewBuilder;

    public MapTable_htm(IHtmlViewBuilder<V> valueViewBuilder) {
        super(Map.class);
        this.valueViewBuilder = valueViewBuilder;
    }

    @Override
    public void precompile(IHtmlViewContext ctx, IUiRef<Map<?, V>> ref) {
        for (Entry<?, V> entry : ref.get().entrySet()) {
            UiValue<V> valueRef = UiValue.wrap(entry.getValue());
            valueViewBuilder.precompile(ctx, valueRef);
        }
    }

    @Override
    public IHtmlOut buildHtmlViewStart(IHtmlViewContext ctx, IHtmlOut out, IUiRef<Map<?, V>> ref)
            throws ViewBuilderException, IOException {
        Map<?, V> map = ref.get();

        out = out.table();

        // this array list is used to keep the value order be same to the keys.
        List<V> values = new ArrayList<>();

        HtmlTr tr = out.thead().tr();
        for (Entry<?, V> entry : map.entrySet()) {
            values.add(entry.getValue());
            Object key = entry.getKey();
            tr.td().text(key.toString());
        }

        tr = out.tbody().tr();
        for (V value : values) {
            UiValue<V> valueRef = UiValue.wrap(value);
            HtmlTd td = tr.td();
            valueViewBuilder.buildHtmlViewStart(ctx, out, valueRef);
        }
        return out;
    }

}
