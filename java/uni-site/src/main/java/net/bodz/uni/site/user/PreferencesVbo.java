package net.bodz.uni.site.user;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;

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
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<Preferences> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Preferences preferences = entry.get();

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
