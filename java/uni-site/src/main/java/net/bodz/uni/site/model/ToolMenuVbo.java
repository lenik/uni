package net.bodz.uni.site.model;

import java.io.IOException;

import net.bodz.bas.gui.dom1.IGUIRefEntry;
import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.PropertyGUIRefEntry;
import net.bodz.bas.potato.ref.PropertyGUIRefMap;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;

public class ToolMenuVbo
        extends AbstractHtmlViewBuilder<ToolMenu> {

    public ToolMenuVbo() {
        super(ToolMenu.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IGUIRefEntry<ToolMenu> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        PropertyGUIRefMap propMap = explode(ref);

        // makeOutmostTag(ctx, "ul", entry.getStyle());
        out.ul().class_("ui-menubox").start();

        for (PropertyGUIRefEntry<Object> prop : propMap.values()) {
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
