package net.bodz.uni.site;

import javax.servlet.ServletContext;

import net.bodz.bas.http.ctx.AbsoluteHref;
import net.bodz.bas.http.ctx.ThreadServletContext;

public class UniSiteHrefs {

    AbsoluteHref webApp;
    AbsoluteHref img;
    AbsoluteHref js;

    public UniSiteHrefs() {
        ServletContext servletContext = ThreadServletContext.getServletContext();
        String contextPath = servletContext.getContextPath();
        webApp = new AbsoluteHref(contextPath.isEmpty() ? "/" : contextPath);
        img = new AbsoluteHref(contextPath + "/img");
        js = new AbsoluteHref(contextPath + "/js");
    }

    private static UniSiteHrefs instance;

    public static UniSiteHrefs getInstance() {
        if (instance == null) {
            synchronized (UniSiteHrefs.class) {
                if (instance == null) {
                    instance = new UniSiteHrefs();
                }
            }
        }
        return instance;
    }

}
