package net.bodz.uni.site;

import java.io.IOException;

import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.html.AbstractHtmlViewBuilder;
import net.bodz.bas.repr.html.IHtmlOutputContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.mda.xjdoc.ClassDocLoader;
import net.bodz.mda.xjdoc.model.ClassDoc;

public class UniSectionVbo
        extends AbstractHtmlViewBuilder<UniSection> {

    public UniSectionVbo() {
        super(UniSection.class);
    }

    @Override
    public IHtmlOutputContext buildHtmlView(IHtmlOutputContext ctx, IRefEntry<UniSection> entry, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        UniSection section = entry.get();

        out.h1().text(section.getLabel());

        ClassDoc classDoc = ClassDocLoader.load(section.getClass());
        out.p().text(classDoc.getTag("title"));

        // PropertyGUIRefMap refMap = explode(site);
        // refMap.importProperties();

        out.dl().start();
        for (UniProject project : section.getProjects()) {
            out.a().href(project.getName() + "/").start();
            out.dt().text(project.getName());
            out.endTag();

            out.dd().text(project.getDescription());
            out.hr();
        }
        out.endTag();

        out.p().text(classDoc.getTag("copyright"));
        return ctx;
    }

}
