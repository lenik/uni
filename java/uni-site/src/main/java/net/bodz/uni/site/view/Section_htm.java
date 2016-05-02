package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.io.IHtmlOut;
import net.bodz.bas.html.io.tag.HtmlDiv;
import net.bodz.bas.html.viz.AbstractHtmlViewBuilder;
import net.bodz.bas.html.viz.IHtmlViewContext;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.IUniSiteAnchors;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.model.Section;

public class Section_htm
        extends AbstractHtmlViewBuilder<Section>
        implements IUniSiteAnchors {

    public Section_htm() {
        super(Section.class);
    }

    @Override
    public void precompile(IHtmlViewContext ctx, IUiRef<Section> ref) {
        super.precompile(ctx, ref);
    }

    @Override
    public IHtmlOut buildHtmlViewStart(IHtmlViewContext ctx, IHtmlOut out, IUiRef<Section> ref)
            throws ViewBuilderException, IOException {
        if (fn.redirect.addSlash(ctx, ref))
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
            HtmlDiv barDiv = out.div().class_("project-bar");

            String letterBoxClass = "letter-box";
            String letter = name.substring(0, 1).toUpperCase();
            if (letter.equals(lastLetter)) {
                letterIndex++;
                letterBoxClass += " ui-hidden";
            } else {
                letterIndex = 0;
                lastLetter = letter;
            }

            HtmlDiv letterDiv = barDiv.div().class_(letterBoxClass).text(letter);
            if (letterIndex == 0)
                letterDiv.id(letter);

            HtmlDiv projectDiv = barDiv.div().class_("project");
            projectDiv.a().class_("resource").href(name + "/").text(name);
            embed(ctx, projectDiv, project.getStat());
            projectDiv.text(project.getLabel());
        }
        return out;
    }

}
