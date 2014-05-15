package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.UiPropertyRef;
import net.bodz.bas.potato.ref.UiPropertyRefMap;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.ToolMenu;

public class ToolMenuVbo
        extends AbstractHtmlViewBuilder<ToolMenu> {

    public ToolMenuVbo() {
        super(ToolMenu.class);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<ToolMenu> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        UiPropertyRefMap propMap = explode(ref);

        // makeOutmostTag(ctx, "ul", entry.getStyle());
        out.ul().class_("ui-menubox").start();

        for (UiPropertyRef<Object> prop : propMap.values()) {
            iString label = prop.getLabel();
            if (label == null)
                continue;

            out.div().class_("ui-caption").text(label);
            out.li().start();
            embed(ctx, prop);
            out.end(); // <div.ui-menuitem>
        }

        out.end(); // <span.ui-menubox>
        return ctx;
    }

}
