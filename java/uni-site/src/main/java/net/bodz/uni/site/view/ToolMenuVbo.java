package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlLiTag;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.potato.ref.UiHelper;
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
    public IHtmlTag buildHtmlView(IHtmlViewContext ctx, IHtmlTag out, IUiRef<ToolMenu> ref, IOptions options)
            throws ViewBuilderException, IOException {
        UiPropertyRefMap propMap = UiHelper.explode(ref);

        // makeOutmostTag(ctx, "ul", entry.getStyle());
        out = out.ul().class_("ui-menubox");

        for (UiPropertyRef<Object> prop : propMap.getEntries()) {
            iString label = prop.getLabel();
            if (label == null)
                continue;

            out.div().class_("ui-caption").text(label);

            HtmlLiTag li = out.li();
            embed(ctx, li, prop);
        }

        return out;
    }

}
