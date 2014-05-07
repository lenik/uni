package net.bodz.uni.site.view;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import net.bodz.bas.html.AbstractHtmlViewBuilder;
import net.bodz.bas.html.IHttpReprContext;
import net.bodz.bas.io.html.IHtmlOut;
import net.bodz.bas.potato.provider.bean.BeanProperty;
import net.bodz.bas.potato.ref.PropertyRefEntry;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.t.tree.IPathInfo;
import net.bodz.bas.ui.dom1.IUiRef;
import net.bodz.mda.xjdoc.model.IElementDoc;
import net.bodz.mda.xjdoc.model.javadoc.IXjdocAware;
import net.bodz.uni.site.IBasePaths;

public class EnumVbo
        extends AbstractHtmlViewBuilder<Enum<?>>
        implements IBasePaths {

    public EnumVbo() {
        super(Enum.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IHttpReprContext buildHtmlView(IHttpReprContext ctx, IUiRef<Enum<?>> ref, IOptions options)
            throws ViewBuilderException, IOException {
        IHtmlOut out = ctx.getOut();
        Enum<?> value = ref.get();

        Class<?> enumType = ref.getValueType();
        boolean xjdocAware = IXjdocAware.class.isAssignableFrom(enumType);

        String id = IPathInfo.fn.getFullPath(ref).replace('/', '.');

        PropertyRefEntry<?> propRef = (PropertyRefEntry<?>) ref;
        BeanProperty property = (BeanProperty) propRef.getProperty();
        Method setter = property.getPropertyDescriptor().getWriteMethod();
        String setterPath = IPathInfo.fn.getFullPath(ref.getParent()) + "/" + setter.getName();

        out.ul().id(id).class_("ui-enum").attr("path", _webApp_ + setterPath).start();

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
            out.a().attr("value", name).text(text);
            out.end(); // <li>
        }

        out.end(); // <ul.enums>
        return ctx;
    }

}
