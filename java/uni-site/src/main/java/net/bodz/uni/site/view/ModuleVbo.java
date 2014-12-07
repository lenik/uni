package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.t.project.IJazzModule;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.mda.xjdoc.Xjdocs;
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

        ClassDoc doc = Xjdocs.getDefaultProvider().getClassDoc(module.getClass());

        IHtmlTag out = ctx.getOut();
        out.h1().text(module.getName());
        out.text("");

        if (doc != null)
            out.verbatim(doc.getText().toString());

        return ctx;
    }

}
