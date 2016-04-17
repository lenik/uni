package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.io.IHtmlOut;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlHeadData;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.model.Preferences;

public class Preferences_htm
        extends AbstractHtmlViewBuilder<Preferences> {

    public Preferences_htm() {
        super(Preferences.class);
    }

    @Override
    public void preview(IHtmlViewContext ctx, IUiRef<Preferences> ref) {
        IHtmlHeadData metaData = ctx.getHeadData();
        metaData.setTitle("User Preferences");
    }

    @Override
    public IHtmlOut buildHtmlViewStart(IHtmlViewContext ctx, IHtmlOut out, IUiRef<Preferences> ref)
            throws ViewBuilderException, IOException {
        Preferences preferences = ref.get();

        out.h1().text("User Preferences");

        out = out.dl();
        out.dt().text("Theme: ");
        out.dd().text(preferences.getTheme().toString());
        out.dt().text("Language: ");
        out.dd().text(preferences.getLanguage().toString());
        return out;
    }

}
