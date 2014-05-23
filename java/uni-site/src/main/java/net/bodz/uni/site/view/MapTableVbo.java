package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.io.html.IHtmlOut;
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
        IHtmlOut out = ctx.getOut();
        Map<?, V> map = ref.get();

        out.table().start();

        // this array list is used to keep the value order be same to the keys.
        List<V> values = new ArrayList<>();

        out.thead().start();
        out.tr().start();
        for (Entry<?, V> entry : map.entrySet()) {
            values.add(entry.getValue());
            Object key = entry.getKey();
            out.td().start();
            out.text(key.toString());
            out.end(); // </td>
        }
        out.end(); // </tr>
        out.end(); // </thead>

        out.tbody().start();
        out.tr().start();
        for (V value : values) {
            UiValue<V> valueRef = UiValue.wrap(value);
            out.td().start();
            valueViewBuilder.buildHtmlView(ctx, valueRef, options);
            out.end(); // </td>
        }
        out.end(); // </tr>
        out.end(); // </tbody>
        return ctx;
    }
}
