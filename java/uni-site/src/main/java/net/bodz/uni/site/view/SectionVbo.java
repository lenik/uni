package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.model.Section;

public class SectionVbo
        extends AbstractHtmlViewBuilder<Section> {

    public SectionVbo() {
        super(Section.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<Section> entry, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        Section section = entry.get();

        String name = section.getName();
        String description = section.getDescription().toString();

        out.h1().textf("Section %s", name);
        if (description != null)
            out.p().text(description);

        String lastLetter = "";

        for (Project project : section.getProjects()) {
            name = project.getName();
            description = project.getDescription().toString();

            out.div().class_("uni-project").start();

            String letter = name.substring(0, 1).toUpperCase();
            String letterBoxClass = "letter-box";
            if (letter.equals(lastLetter))
                letterBoxClass += " ui-hidden";
            lastLetter = letter;
            out.div().class_(letterBoxClass).text(letter);

            out.div().class_("prj-description").start();

            out.a().href(name + "/").text(name);

            out.div().class_("prj-status").start();
            out.text("♥ 0 ♬ 0 ⇵ 0");
            out.end(); // <div#prj-status>

            out.println(description.replace("\n", "<br>\n"));
            out.end();

            out.end(); // <div#uni-project>
        }

        return ctx;
    }
}
