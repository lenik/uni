package net.bodz.uni.site;

import net.bodz.bas.http.ctx.IAnchor;
import net.bodz.bas.site.IBasicSiteAnchors;

public interface IUniSiteAnchors
        extends IBasicSiteAnchors {

    IAnchor _img_ = _webApp_.join("img/");

}
