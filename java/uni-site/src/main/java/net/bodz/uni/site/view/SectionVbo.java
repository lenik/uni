package net.bodz.uni.site.view;

import java.io.IOException;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.i18n.dom.iString;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.io.html.tag.HtmlDivBuilder;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.uni.site.IBasePaths;
import net.bodz.uni.site.model.Project;
import net.bodz.uni.site.model.Section;

public class SectionVbo
        extends AbstractHtmlViewBuilder<Section>
        implements IBasePaths {

    public SectionVbo() {
        super(Section.class);
    }

    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<Section> ref, IOptions options)
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
            description = project.getDescription();

            out.div().class_("uni-project").start();

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

            out.a().class_("download").href(name + "/").text(name);

            embed(ctx, project.getStat());

            out.println(description.toString().replace("\n", "<br>\n"));
            out.end();

            out.end(); // <div#uni-project>
        }

        return ctx;
    }
}
