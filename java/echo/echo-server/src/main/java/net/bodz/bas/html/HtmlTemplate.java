package net.bodz.bas.html;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.io.res.IStreamResource;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class HtmlTemplate
        extends AbstractHtmlTemplate {

    static Logger logger = LoggerFactory.getLogger(HtmlTemplate.class);

    public HtmlTemplate() {
        super();
    }

    public HtmlTemplate(Writer writer) {
        super(writer);
    }

    @Override
    protected void instantiate() {
        if (isFragment()) {
            _pageContentWrapper();
        } else {
            html();
            _header();
            _body();
            end();
        }
    }

    protected boolean isFragment() {
        return false;
    }

    protected void _header() {
        head();
        // meta().httpEquiv("content-type").content("text/html; encoding=utf-8");
        title().text(getTitle()).end();
        String css = getCss().trim();
        if (!css.isEmpty()) {
            style().type("text/css").text(css).end(); // OPT: Indentation?
        }
        end();
    }

    protected void _pageHeader() {
        h1().text(getTitle()).end();
    }

    protected void _pageFooter() {
        div().classAttr("footer");
        text(getCopyright());
        end();
    }

    protected void _body() {
        body();
        {
            div().classAttr("container");
            _pageLayout();
            end();
        }
        end();
    }

    protected void _pageLayout() {
        div().classAttr("header");
        _pageHeader();
        end();

        div().classAttr("content-container");
        {
            div().classAttr("content");
            _pageContentWrapper();
            end();
        }
        end();

        div().classAttr("footer");
        _pageFooter();
        end();
    }

    protected void _pageContentWrapper() {
        try {
            _pageContent();
        } catch (Exception e) {
            logger.error("Failed to render the page: " + e.getMessage(), e);
            h3().text("Failed to render the page: " + e.getMessage()).end();
            String errorHtml = ExceptionFormat.highlight(e);
            try {
                out.write("Stacktrace: <pre>" + errorHtml + "</pre>\n");
            } catch (IOException e1) {
                throw new RuntimeException(e1.getMessage(), e1);
            }
        }
    }

    protected void _pageContent()
            throws Exception {
        p().text("No content").end();
    }

    public String getTitle() {
        return getClass().getName();
    }

    public String getCopyright() {
        return "";
    }

    protected IStreamResource getClassResource(String ext) {
        return getClassResource(getClass(), ext, true);
    }

    private static IStreamResource getClassResource(Class<?> clazz, String ext, boolean inheritable) {
        if (clazz == null)
            throw new NullPointerException("clazz");
        if (ext == null)
            throw new NullPointerException("ext");
        String resourceName = clazz.getSimpleName() + ext;
        URL url = clazz.getResource(resourceName);
        if (url != null)
            return new URLResource(url);
        if (inheritable) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null)
                return getClassResource(superclass, ext, inheritable);
        }
        return null;
    }

    protected String getCss() {
        StringBuilder buf = new StringBuilder();

        // parse...??
        String classCss = getClassCss();
        if (classCss != null)
            buf.append(classCss);

        Map<String, String> styleMap = new LinkedHashMap<String, String>();
        populateStyles(styleMap);
        for (Entry<String, String> styleEntry : styleMap.entrySet()) {
            String styleName = styleEntry.getKey();
            String styleDef = styleEntry.getValue();
            buf.append(styleName);
            buf.append(" { ");
            buf.append(styleDef);
            buf.append(" };\n");
        }
        return buf.toString();
    }

    protected void populateStyles(Map<String, String> styleMap) {
    }

    protected String getClassCss() {
        IStreamResource cssResource = getClassResource(".css");
        if (cssResource == null)
            return null;
        else
            try {
                return cssResource.tooling()._for(StreamReading.class).readString();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
    }

}
