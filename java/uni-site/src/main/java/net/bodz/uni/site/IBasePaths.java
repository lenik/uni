package net.bodz.uni.site;

import net.bodz.bas.http.ctx.IBasePath;
import net.bodz.bas.http.ctx.WebAppPath;

public interface IBasePaths {

    IBasePath _webjars_ = new WebAppPath("/webjars");

    IBasePath _fonts_ = new WebAppPath("/fonts");

    IBasePath _webApp_ = new WebAppPath("/");

    IBasePath _img_ = new WebAppPath("/img");

    IBasePath _js_ = new WebAppPath("/js");

}
