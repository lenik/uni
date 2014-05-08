package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.Preferences;

public class PreferencesVbo
        extends AbstractHtmlViewBuilder<Preferences> {

    public PreferencesVbo() {
        super(Preferences.class);
    }

    @Override
    public void buildTitle(StringBuilder buffer, Preferences value) {
        buffer.append("User Preferences");
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<Preferences> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Preferences preferences = ref.get();

        out.h1().text("User Preferences");

        out.dl().start();
        out.dt().text("Theme: ");
        out.dd().text(preferences.getTheme().toString());
        out.dt().text("Language: ");
        out.dd().text(preferences.getLanguage().toString());
        out.end();

        return ctx;
    }

}