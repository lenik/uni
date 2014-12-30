package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlDivTag;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.i18n.dom.iString;
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
    public IHtmlTag buildHtmlView(IHtmlViewContext ctx, IHtmlTag out, IUiRef<Section> ref, IOptions options)
            throws ViewBuilderException, IOException {
        if (enter(ctx))
            return null;

        Section section = ref.get();

        String name = section.getName();
        iString description = section.getDescription();

        out.h1().a().text("Section " + name);

        if (description != null) {
            out.div().class_("description").verbatim(description.toString());
        }

        String lastLetter = "";
        int letterIndex = 0;

        for (Project project : section.getProjects()) {
            name = project.getName();
            if (project.isPrivate())
                continue;
            HtmlDivTag barDiv = out.div().class_("project-bar");

            String letterBoxClass = "letter-box";
            String letter = name.substring(0, 1).toUpperCase();
            if (letter.equals(lastLetter)) {
                letterIndex++;
                letterBoxClass += " ui-hidden";
            } else {
                letterIndex = 0;
                lastLetter = letter;
            }

            HtmlDivTag letterDiv = barDiv.div().class_(letterBoxClass).text(letter);
            if (letterIndex == 0)
                letterDiv.id(letter);

            HtmlDivTag projectDiv = barDiv.div().class_("project");
            projectDiv.a().class_("resource").href(name + "/").text(name);
            embed(ctx, projectDiv, project.getStat());
            projectDiv.text(project.getLabel());
        }

        return out;
    }
}
