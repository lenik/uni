package net.bodz.uni.site.view;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import net.bodz.bas.html.dom.IHtmlTag;
import net.bodz.bas.html.dom.tag.HtmlATag;
import net.bodz.bas.html.dom.tag.HtmlLiTag;
import net.bodz.bas.html.dom.tag.HtmlUlTag;
import net.bodz.bas.html.viz.AbstractHttpViewBuilder;
import net.bodz.bas.html.viz.IHttpViewContext;
import net.bodz.bas.potato.provider.bean.BeanProperty;
import net.bodz.bas.potato.ref.PropertyRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.t.tree.IPathInfo;
import net.bodz.bas.typer.std.IAttributes;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;
import net.bodz.uni.site.IUniSiteAnchors;

public class Enum_htm
        extends AbstractHttpViewBuilder<Enum<?>>
        implements IUniSiteAnchors {

    public Enum_htm() {
        super(Enum.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IHtmlTag buildHtmlView(IHttpViewContext ctx, IHtmlTag out, IUiRef<Enum<?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        Enum<?> value = ref.get();

        Class<?> enumType = ref.getValueType();
        boolean xjdocAware = IXjdocAware.class.isAssignableFrom(enumType);
        boolean attributed = IAttributes.class.isAssignableFrom(enumType);

        String id = IPathInfo.fn.getFullPath(ref).replace('/', '.');

        PropertyRefEntry<?> propRef = (PropertyRefEntry<?>) ref;
        BeanProperty property = (BeanProperty) propRef.getProperty();
        Method setter = property.getPropertyDescriptor().getWriteMethod();
        String setterPath = IPathInfo.fn.getFullPath(ref.getParent()) + "/" + setter.getName();

        HtmlUlTag ul = out.ul().id(id).class_("ui-enum").attr("path", _webApp_ + setterPath);

        for (Object _item : EnumSet.allOf((Class) enumType)) {
            Enum<?> item = (Enum<?>) _item;
            String name = item.name();
            boolean active = item == value;
            String text = name;

            if (xjdocAware) {
                IElementDoc doc = ((IXjdocAware) item).getXjdoc();
                text = doc.getText().toString();
            }

            HtmlLiTag li = ul.li().class_(active ? "ui-active" : "");

            HtmlATag a = li.a().attr("value", name);
            if (attributed) {
                IAttributes attributes = (IAttributes) item;
                for (String attrName : attributes.getAttributeNames())
                    a.attr(attrName, attributes.getAttribute(attrName));
            }
            a.text(text);
        }
        return out;
    }

}
