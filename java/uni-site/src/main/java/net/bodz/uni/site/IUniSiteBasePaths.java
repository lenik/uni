package net.bodz.uni.site;

import net.bodz.bas.http.ctx.IBasePath;
import net.bodz.bas.http.ctx.WebAppPath;

public interface IUniSiteBasePaths {

    IBasePath _webApp_ = new WebAppPath("/");

    IBasePath _img_ = new WebAppPath("/img");

    IBasePath _js_ = new WebAppPath("/js");

}
