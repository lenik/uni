    file "src/main/java/$dir_web/${Base}Index_htm.java"

    cat <<EOT >"$file"
package $pkg_web;

import java.io.IOException;
import java.util.List;

import net.bodz.bas.c.reflect.NoSuchPropertyException;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.html.dom.tag.HtmlDivTag;
import net.bodz.bas.html.dom.tag.HtmlTrTag;
import net.bodz.bas.html.util.IFontAwesomeCharAliases;
import net.bodz.bas.html.viz.IHttpViewContext;
import net.bodz.bas.repr.viz.ViewBuilderException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.ui.dom1.IUiRef;

import com.tinylily.model.base.security.User;

import $fqcn;

public class ${Base}Index_htm
        extends SlimIndex_htm<${Base}Index, $Base, ${Base}Mask> {

    public ${Base}Index_htm()
            throws NoSuchPropertyException, ParseException {
        super(${Base}Index.class);
        indexFields.parse("i*sa", "label", "description");
    }


    @Override
    protected ${Base}Mask buildSwitchers(IHttpViewContext ctx, SwitcherModelGroup switchers)
            throws ViewBuilderException {
        // ${Base}Mapper mapper = ctx.query(${Base}Mapper.class);
        ${Base}Mask mask = fn.maskFromRequest(new ${Base}Mask(), ctx.getRequest());

        SwitcherModel<Integer> sw;
        sw = switchers.entityOf("表单", true, //
                ctx.query(FormDefMapper.class).filter(FormDefMask.all()), //
                "form", mask.formId, mask.noForm);
        mask.formId = sw.getSelection1();
        mask.noForm = sw.isSelectNull();

        return mask;
    }

    @Override
    public void dataIndex(IHttpViewContext ctx, DataViewAnchors<$Base> a, IUiRef<${Base}Index> ref, IOptions options)
            throws ViewBuilderException, IOException {
        ${Base}Mapper mapper = ctx.query(${Base}Mapper.class);
        ${Base}Mask mask = ctx.query(${Base}Mask.class);
        List<${Base}> list = postfilt(mapper.filter(mask));

        IndexTable itab = new IndexTable(a.data);
        itab.buildHeader(ctx, indexFields.values());
        if (a.dataList())
            for (${Base} o : list) {
                HtmlTrTag tr = itab.tbody.tr();
                itab.cocols("i", tr, o);
                itab.cocols("u", tr, o);
                itab.cocols("sa", tr, o);
            }

        if (a.extradata != null)
            dumpFullData(a.extradata, list);
    }

}
EOT

    css="src/main/resources/$dir_web/${Base}Index_htm.css"
    js="src/main/resources/$dir_web/${Base}Index_htm.js"
    touch $css
    touch $js

    add_inf \
        "src/main/resources/META-INF/services/net.bodz.bas.http.viz.IHttpViewBuilder" \
        "$pkg_web.${Base}Index_htm"
