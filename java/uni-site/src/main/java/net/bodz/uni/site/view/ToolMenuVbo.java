package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
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
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<ToolMenu> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        ToolMenu menu = ref.get();

        out.div().class_("ui-menubox").start();
        out.div().class_("ui-caption").text("Theme");

        out.div().class_("ui-menuitem").start();
        menu.getTheme();
        out.end(); // <div.ui-menuitem>

        out.div().class_("ui-caption").text("Language");

        out.div().class_("ui-menuitem").start();
        menu.getLanguage();
        out.end(); // <div.ui-menuitem>

        out.div().class_("ui-caption").text("Caching");
        out.div().class_("ui-menuitem").text("ON OFF");

        out.end(); // <span.ui-menubox>

        return ctx;
    }

}
