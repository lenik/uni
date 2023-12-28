
switch (location.protocol) {
    case "file:":
        if (location.pathname.startsWith("/android_asset/"))
            ; // debug = debug;
        else
            window.debug = true;
        break;
    case "http:":
    case "https:":
        if (location.host == "localhost")
            window.debug = true;
        break;
}

(function($) {

    var _ajax = $.ajax;
    $.ajax = function(params) {
        if ((typeof params) == 'string') {
            return _ajax(alterHref(params));
        }
        params.url = alterHref(params.url);
        return _ajax(params);
    };

    window.setLocationHref = function(href) {
        href = alterHref(href);
        window.location.href = href;
    };

})(jQuery);
