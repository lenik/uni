import $ from 'jquery';

import * as Url from '../io/url';

var win: any = window;

switch (location.protocol) {
    case "file:":
        if (location.pathname.startsWith("/android_asset/"))
            ; // debug = debug;
        else
            win.debug = true;
        break;
    case "http:":
    case "https:":
        if (location.host == "localhost")
            win.debug = true;
        break;
}

var _ajax = $.ajax;
$.ajax = function(params: any) {
    if ((typeof params) == 'string') {
        return _ajax(Url.alterHref(params));
    }
    params.url = Url.alterHref(params.url);
    return _ajax(params);
};

win.setLocationHref = (href: string) => {
    href = Url.alterHref(href);
    window.location.href = href;
};
