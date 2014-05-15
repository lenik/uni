package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHtmlViewContext;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.io.html.tag.HtmlDivBuilder;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.IUniSiteAnchors;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.model.Section;

public class SectionVbo
        extends AbstractHtmlViewBuilder<Section>
        implements IUniSiteAnchors {

    public SectionVbo() {
        super(Section.class);
    }

    @Override
    public void preview(IHtmlViewContext ctx, IUiRef<Section> ref, IOptions options) {
        super.preview(ctx, ref, options);
    }

    @Override
    public IHtmlViewContext buildHtmlView(IHtmlViewContext ctx, IUiRef<Section> ref, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        IHtmlOut out = ctx.getOut();
        Section section = ref.get();

        String name = section.getName();
        iString description = section.getDescription();

        out.h1().start();
        out.a().href(_webApp_ + "#" + section.getName()).text("Section " + name);
        out.end(); // <h1>

        if (description != null) {
            out.div().class_("description").start();
            out.verbatim(description.toString());
            out.end();
        }

        String lastLetter = "";
        int letterIndex = 0;

        for (Project project : section.getProjects()) {
            name = project.getName();
            if (project.isPrivate())
                continue;
            out.div().class_("project-bar").start();

            String letterBoxClass = "letter-box";
            String letter = name.substring(0, 1).toUpperCase();
            if (letter.equals(lastLetter)) {
                letterIndex++;
                letterBoxClass += " ui-hidden";
            } else {
                letterIndex = 0;
                lastLetter = letter;
            }

            HtmlDivBuilder letterDiv = out.div().class_(letterBoxClass).text(letter);
            if (letterIndex == 0)
                letterDiv.id(letter);

            out.div().class_("project").start();
            out.a().class_("resource").href(name + "/").text(name);
            embed(ctx, project.getStat());
            out.println(project.getLabel());
            out.end(); // <div.project>

            out.end(); // <div.project-bar>
        }

        return ctx;
    }
}
