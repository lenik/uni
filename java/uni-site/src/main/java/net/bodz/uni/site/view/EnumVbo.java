package net.bodz.uni.site.view;

import java.io.IOException;
import java.util.EnumSet;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.ref.IRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;

public class EnumVbo
        extends AbstractHtmlViewBuilder<Enum<?>> {

    public EnumVbo() {
        super(Enum.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IRefEntry<Enum<?>> entry, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Enum<?> value = entry.get();

        Class<? extends Enum<?>> enumType = entry.getValueType();
        boolean xjdocAware = IXjdocAware.class.isAssignableFrom(enumType);

        out.ul().class_("ui-enums").start();

        for (Object _item : EnumSet.allOf((Class) enumType)) {
            Enum<?> item = (Enum<?>) _item;
            String name = item.name();
            boolean active = item == value;
            String text = name;

            if (xjdocAware) {
                IElementDoc doc = ((IXjdocAware) item).getXjdoc();
                text = doc.getText().getHeadPar();
            }

            out.li().class_(active ? "ui-active" : "").start();

            String onclick = String.format("setTheme(\"%s\")", item.name());
            out.a().attr("value", name).onclick(onclick).text(text);

            out.end(); // <li>
        }

        out.end(); // <ul.enums>
        return ctx;
    }

}
