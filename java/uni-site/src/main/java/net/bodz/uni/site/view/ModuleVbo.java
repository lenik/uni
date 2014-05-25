package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.t.project.IJazzModule;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.ClassDoc;

public class ModuleVbo
        extends AbstractHtmlViewBuilder<IJazzModule> {

    public ModuleVbo() {
        super(IJazzModule.class);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<IJazzModule> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IJazzModule module = ref.get();

        ClassDoc moduleDoc = ClassDocLoader.load(module.getClass());

        IHtmlTag out = ctx.getOut();
        out.h1().text(module.getName());
        out.text("");

        out.verbatim(moduleDoc.getText().toString());

        return ctx;
    }

}
